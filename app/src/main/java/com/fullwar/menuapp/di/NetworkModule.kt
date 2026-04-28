package com.fullwar.menuapp.di

import android.content.Context
import android.provider.Settings
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.model.ApiErrorResponseDto
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.data.model.ApiValidationErrorResponseDto
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.data.util.AuthEventBus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import com.fullwar.menuapp.data.repository.SecureCookiesStorageImpl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {

    // Cliente PÚBLICO: sin token, utilizado para login / refresh / logout
    single(named("PublicClient")) {
        val context = get<Context>()
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        HttpClient(Android) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpCookies) {
                storage = get<SecureCookiesStorageImpl>()
            }
            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status.value !in 200..299) {
                        if (response.status.value == 422) {
                            val validationError = try {
                                response.body<ApiValidationErrorResponseDto>()
                            } catch (e: Exception) { null }

                            throw ApiException(
                                statusCode = 422,
                                errorDetail = validationError?.detail,
                                errorCode = validationError?.code,
                                validationErrors = validationError?.errors
                            )
                        }

                        val errorBody = try {
                            response.body<ApiErrorResponseDto>()
                        } catch (e: Exception) { null }

                        throw ApiException(
                            statusCode = response.status.value,
                            errorDetail = errorBody?.detail,
                            errorCode = errorBody?.code
                        )
                    }
                }
            }
            defaultRequest {
                url(Constants.BASE_URL)
                contentType(ContentType.Application.Json)
                header("DeviceId", deviceId)
            }
        }
    }

    // Cliente PRIVADO: inyecta Bearer token en cada petición, con refresh automático en 401
    single(named("AuthClient")) {
        val context = get<Context>()
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val tokenProvider = get<TokenProvider>()
        val authRepository = get<AuthRepositoryImpl>()
        val refreshMutex = Mutex()

        HttpClient(Android) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            HttpResponseValidator {
                validateResponse { response ->
                    // El 401 lo maneja el interceptor HttpSend — no lanzar excepción aquí para ese código
                    if (response.status.value !in 200..299 && response.status.value != 401) {
                        if (response.status.value == 422) {
                            val validationError = try {
                                response.body<ApiValidationErrorResponseDto>()
                            } catch (e: Exception) { null }

                            throw ApiException(
                                statusCode = 422,
                                errorDetail = validationError?.detail,
                                errorCode = validationError?.code,
                                validationErrors = validationError?.errors
                            )
                        }

                        val errorBody = try {
                            response.body<ApiErrorResponseDto>()
                        } catch (e: Exception) { null }

                        throw ApiException(
                            statusCode = response.status.value,
                            errorDetail = errorBody?.detail,
                            errorCode = errorBody?.code
                        )
                    }
                }
            }
            defaultRequest {
                url(Constants.BASE_URL)
                contentType(ContentType.Application.Json)
                header("DeviceId", deviceId)
                tokenProvider.getToken()?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
        }.also { client ->
            client.plugin(HttpSend).intercept { request ->
                val originalCall = execute(request)

                if (originalCall.response.status != HttpStatusCode.Unauthorized) {
                    return@intercept originalCall
                }

                refreshMutex.withLock {
                    val currentToken = tokenProvider.getToken()
                    val sentToken = request.headers["Authorization"]?.removePrefix("Bearer ")

                    if (currentToken != null && currentToken != sentToken) {
                        request.headers.remove("Authorization")
                        request.headers.append("Authorization", "Bearer $currentToken")
                        return@withLock execute(request)
                    }

                    try {
                        authRepository.refreshAsync()
                        val newToken = tokenProvider.getToken()
                        request.headers.remove("Authorization")
                        request.headers.append("Authorization", "Bearer $newToken")
                        execute(request)
                    } catch (e: Exception) {
                        try {
                            authRepository.logoutAsync()
                        } catch (logoutException: Exception) {
                            android.util.Log.w("NetworkModule", "logoutAsync falló en interceptor: ${logoutException.message}")
                        } finally {
                            AuthEventBus.emitSessionExpired()
                        }
                        originalCall
                    }
                }
            }
        }
    }
}
