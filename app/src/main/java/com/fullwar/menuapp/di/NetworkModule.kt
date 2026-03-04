package com.fullwar.menuapp.di

import com.fullwar.menuapp.data.datasource.local.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
            defaultRequest {
                url("https://menu-soda-dev.up.railway.app/")
                contentType(ContentType.Application.Json)
            }
        }
    }

    // Cliente PRIVADO: inyecta Bearer token en cada petición
    single(named("AuthClient")) {
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
            defaultRequest {
                url("https://menu-soda-dev.up.railway.app/")
                contentType(ContentType.Application.Json)
                tokenProvider.getToken()?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
        }
    }
}
