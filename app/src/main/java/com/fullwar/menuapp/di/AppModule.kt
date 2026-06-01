package com.fullwar.menuapp.di

import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.datasource.remote.RecuperarContrasenaService
import com.fullwar.menuapp.data.datasource.remote.EntradaService
import com.fullwar.menuapp.data.datasource.remote.ImagenService
import com.fullwar.menuapp.data.datasource.remote.MenuDiarioService
import com.fullwar.menuapp.data.datasource.remote.MenuImagenService
import com.fullwar.menuapp.data.datasource.remote.UsuarioService
import com.fullwar.menuapp.data.datasource.remote.PlatoService
import com.fullwar.menuapp.data.datasource.remote.TipoEntradaService
import com.fullwar.menuapp.data.datasource.remote.TipoPlatoService
import com.fullwar.menuapp.data.repository.RecuperarContrasenaRepositoryImpl
import com.fullwar.menuapp.data.repository.SecureDataStoreImpl
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.data.repository.EntradaRepositoryImpl
import com.fullwar.menuapp.data.repository.MenuDiarioRepositoryImpl
import com.fullwar.menuapp.data.repository.MenuImagenRepositoryImpl
import com.fullwar.menuapp.data.repository.UsuarioRepositoryImpl
import com.fullwar.menuapp.data.repository.PlatoRepositoryImpl
import com.fullwar.menuapp.domain.repository.IRecuperarContrasenaRepository
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.domain.repository.IUsuarioRepository
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.data.repository.LocationProviderImpl
import com.fullwar.menuapp.data.repository.SecureCookiesStorageImpl
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SeleccionEntradasViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.seleccion.SeleccionPlatosFondoViewModel
import com.fullwar.menuapp.presentation.features.menu.estilo.SeleccionEstiloViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.historial.HistorialViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.PerfilViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.informacion_personal.InformacionPersonalViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.cambiar_contrasena.CambiarContrasenaViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena.NuevaContrasenaViewModel
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena.RecuperarContrasenaViewModel
import com.fullwar.menuapp.presentation.features.login.LoginViewModel
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import com.fullwar.menuapp.presentation.features.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SharedViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HistorialViewModel)
    viewModelOf(::PerfilViewModel)
    viewModelOf(::InformacionPersonalViewModel)
    viewModelOf(::CambiarContrasenaViewModel)
    viewModelOf(::RecuperarContrasenaViewModel)
    viewModelOf(::NuevaContrasenaViewModel)
    viewModelOf(::MenuViewModel)
    viewModelOf(::EntradaViewModel)
    viewModelOf(::SeleccionEntradasViewModel)
    viewModelOf(::PlatoViewModel)
    viewModelOf(::SeleccionPlatosFondoViewModel)
    viewModelOf(::SeleccionEstiloViewModel)

    // Almacenamiento seguro cifrado (DataStore + Tink + Android Keystore)
    single<SecureStorageProvider> { SecureDataStoreImpl(androidContext()) }

    // Almacenamiento persistente de cookies (refresh token)
    single { SecureCookiesStorageImpl(get()) }

    // AuthService usa el PublicClient (login / refresh / logout — sin Bearer)
    single { AuthService(get(named("PublicClient")), get()) }

    // RecuperarContrasenaService usa el AuthClient para que el interceptor de 401 gestione refresh/logout
    single { RecuperarContrasenaService(get(named("AuthClient")), get()) }

    // AuthRepositoryImpl implementa TokenProvider
    singleOf(::AuthRepositoryImpl) bind TokenProvider::class

    // RecuperarContrasenaRepositoryImpl usa AuthClient vía RecuperarContrasenaService (sin ciclo)
    singleOf(::RecuperarContrasenaRepositoryImpl) bind IRecuperarContrasenaRepository::class

    // LocationProviderImpl implementa LocationProvider
    singleOf(::LocationProviderImpl) bind LocationProvider::class

    // Entrada feature: services usan AuthClient (Bearer token)
    single { EntradaService(get(named("AuthClient"))) }
    single { TipoEntradaService(get(named("AuthClient"))) }
    single { ImagenService(get(named("AuthClient"))) }
    singleOf(::EntradaRepositoryImpl) bind IEntradaRepository::class

    // Plato feature: services usan AuthClient (Bearer token)
    single { PlatoService(get(named("AuthClient"))) }
    single { TipoPlatoService(get(named("AuthClient"))) }
    singleOf(::PlatoRepositoryImpl) bind IPlatoRepository::class

    // MenuImagen feature: service usa AuthClient (Bearer token)
    single { MenuImagenService(get(named("AuthClient"))) }
    singleOf(::MenuImagenRepositoryImpl) bind IMenuImagenRepository::class

    // MenuDiario feature: service usa AuthClient (Bearer token)
    single { MenuDiarioService(get(named("AuthClient"))) }
    singleOf(::MenuDiarioRepositoryImpl) bind IMenuDiarioRepository::class

    // Usuario feature: service usa AuthClient (Bearer token)
    single { UsuarioService(get(named("AuthClient"))) }
    singleOf(::UsuarioRepositoryImpl) bind IUsuarioRepository::class
}