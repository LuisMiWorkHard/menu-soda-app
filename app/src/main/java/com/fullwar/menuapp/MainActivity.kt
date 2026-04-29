package com.fullwar.menuapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fullwar.menuapp.presentation.features.splash.SplashViewModel
import com.fullwar.menuapp.presentation.navigation.AppScreens
import com.fullwar.menuapp.presentation.navigation.SetupNavigation
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModel()

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale.forLanguageTag("es-ES")
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !splashViewModel.isInitialized }

        enableEdgeToEdge()
        setContent {
            MenuAppTheme {
                if (splashViewModel.isInitialized) {
                    val startDestination = if (splashViewModel.hasValidToken)
                        AppScreens.HomeScreen.route
                    else
                        AppScreens.LoginScreen.route

                    SetupNavigation(startDestination = startDestination)
                }
            }
        }
    }
}
