package com.fullwar.menuapp

import android.app.Application
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.di.networkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

class MainApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, networkModule)
        }

        // Inicializar el caché de token en background
        applicationScope.launch {
            val authRepository = get<AuthRepositoryImpl>(AuthRepositoryImpl::class.java)
            authRepository.initialize()
        }
    }
}