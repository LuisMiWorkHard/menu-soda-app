package com.fullwar.menuapp

import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.presentation.features.login.LoginViewModel
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SharedViewModel)
    viewModelOf(::LoginViewModel)

    // AuthService usa el PublicClient de NetworkModule para llamar al AuthController del backend
    single { AuthService(get(named("PublicClient"))) }

    // AuthRepositoryImpl implementa TokenProvider
    // Koin proveerá AuthRepositoryImpl cuando alguien solicite TokenProvider
    singleOf(::AuthRepositoryImpl) bind TokenProvider::class
}