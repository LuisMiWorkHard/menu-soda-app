package com.fullwar.menuapp.di

import android.content.Context
import android.provider.Settings
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.model.ApiErrorResponseDto
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.data.model.ApiValidationErrorResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
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
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
                url("https://menu-soda-dev.up.railway.app/")
                contentType(ContentType.Application.Json)
                header("DeviceId", deviceId)
            }
        }
    }

    // Cliente PRIVADO: inyecta Bearer token en cada petición
    single(named("AuthClient")) {
        val context = get<Context>()
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val tokenProvider = get<TokenProvider>()

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
                url("https://menu-soda-dev.up.railway.app/")
                contentType(ContentType.Application.Json)
                header("DeviceId", deviceId)
                tokenProvider.getToken()?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
        }
    }
}
