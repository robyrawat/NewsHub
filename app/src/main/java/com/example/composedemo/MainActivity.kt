package com.example.composedemo

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.composedemo.data.preferences.LanguageManager
import com.example.composedemo.data.preferences.ThemeMode
import com.example.composedemo.data.preferences.UserPreferencesManager
import com.example.composedemo.presentation.navigation.NavGraph
import com.example.composedemo.presentation.ui.SplashScreen
import com.example.composedemo.ui.theme.ComposeDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    @Inject
    lateinit var languageManager: LanguageManager

    override fun attachBaseContext(newBase: Context?) {
        // Apply saved language before activity is created
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    private fun updateBaseContextLocale(context: Context?): Context? {
        if (context == null) return null

        return try {
            // Get saved language from preferences synchronously
            // This is a simplified approach - in production you might want to handle this differently
            val sharedPrefs = context.getSharedPreferences("settings_temp", Context.MODE_PRIVATE)
            val savedLanguage = sharedPrefs.getString("language", "en") ?: "en"

            val locale = Locale(savedLanguage)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
                context.createConfigurationContext(config)
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
                @Suppress("DEPRECATION")
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
                context
            }
        } catch (e: Exception) {
            context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Apply saved language on startup
        lifecycleScope.launch {
            try {
                val savedLanguage = preferencesManager.language.first()
                languageManager.applyLanguage(savedLanguage)

                // Save to temp prefs for attachBaseContext
                getSharedPreferences("settings_temp", Context.MODE_PRIVATE)
                    .edit()
                    .putString("language", savedLanguage)
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            var showSplash by remember { mutableStateOf(true) }

            val themeMode by preferencesManager.themeMode.collectAsState(initial = "SYSTEM")
            val fontSize by preferencesManager.fontSize.collectAsState(initial = "medium")

            val isDarkTheme = when (ThemeMode.valueOf(themeMode)) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            ComposeDemoTheme(
                darkTheme = isDarkTheme,
                fontSize = fontSize // Pass dynamic font size
            ) {
                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {
                    NavGraph(navController = rememberNavController())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reapply language in case it was changed
        lifecycleScope.launch {
            try {
                val savedLanguage = preferencesManager.language.first()
                languageManager.applyLanguage(savedLanguage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
