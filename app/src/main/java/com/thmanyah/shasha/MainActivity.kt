package com.thmanyah.shasha

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.thmanyah.shasha.core.util.LocaleManager
import com.thmanyah.shasha.presentation.navigation.AppNavigation
import com.thmanyah.shasha.ui.theme.ThmanyahTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThmanyahTheme {
                AppNavigation(
                    onToggleLanguage = {
                        LocaleManager.toggleLanguage(this)
                        recreate()
                    },
                )
            }
        }
    }
}
