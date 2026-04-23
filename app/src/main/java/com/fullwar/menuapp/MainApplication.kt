package com.fullwar.menuapp

import android.app.Application
import android.provider.Settings
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okio.Path.Companion.toOkioPath
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.di.appModule
import com.fullwar.menuapp.di.networkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

class MainApplication : Application(), SingletonImageLoader.Factory {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, networkModule)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val tokenProvider = get<TokenProvider>(TokenProvider::class.java)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.toOkioPath().resolve("image_cache"))
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .components {
                add(OkHttpNetworkFetcherFactory(
                    callFactory = {
                        OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                val token = runBlocking { tokenProvider.getToken() }
                                val request = chain.request().newBuilder()
                                    .header("DeviceId", deviceId)
                                    .apply { token?.let { header("Authorization", "Bearer $it") } }
                                    .build()
                                chain.proceed(request)
                            }
                            .build()
                    }
                ))
            }
            .build()
    }
}
