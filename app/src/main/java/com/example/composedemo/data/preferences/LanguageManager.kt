package com.example.composedemo.data.preferences

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val context: Context,
    private val preferencesManager: UserPreferencesManager
) {

    companion object {
        private const val TAG = "LanguageManager"

        val SUPPORTED_LANGUAGES = mapOf(
            "en" to "English",
            "es" to "Español",
            "fr" to "Français",
            "de" to "Deutsch",
            "it" to "Italiano",
            "pt" to "Português",
            "ru" to "Русский",
            "zh" to "中文",
            "ja" to "日本語",
            "ko" to "한국어",
            "ar" to "العربية",
            "hi" to "हिन्दी",
            "tr" to "Türkçe",
            "nl" to "Nederlands"
        )

        // NewsData.io actually supports these languages for news content
        // Based on their documentation: https://newsdata.io/documentation/#supported_languages
        val API_LANGUAGE_MAPPING = mapOf(
            "hi" to "hi", // Hindi IS supported by NewsData.io
            "ar" to "ar", // Arabic IS supported
            "zh" to "zh", // Chinese IS supported
            "ja" to "ja", // Japanese IS supported
            "ko" to "ko", // Korean IS supported
            "ru" to "ru", // Russian IS supported
            "tr" to "tr", // Turkish IS supported
            "nl" to "nl", // Dutch IS supported
            "en" to "en",
            "es" to "es",
            "fr" to "fr",
            "de" to "de",
            "it" to "it",
            "pt" to "pt"
        )
    }

    fun applyLanguage(languageCode: String) {
        Log.d(TAG, "Applying language: $languageCode")
        Log.d(TAG, "Current locale before change: ${Locale.getDefault()}")

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            Log.d(TAG, "Set locale using setLocale (API >= N)")
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            Log.d(TAG, "Set locale using deprecated method (API < N)")
        }

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        Log.d(TAG, "Language applied. Current locale: ${Locale.getDefault()}")
        Log.d(TAG, "Configuration locale: ${config.locales[0]}")
    }

    fun restartApp() {
        Log.d(TAG, "Restarting app for language change")
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intent != null) {
            Log.d(TAG, "Starting new activity with intent: $intent")
            context.startActivity(intent)
            if (context is android.app.Activity) {
                context.finish()
            }

            // For better user experience, we can also terminate the process
            android.os.Process.killProcess(android.os.Process.myPid())
        } else {
            Log.e(TAG, "Failed to get launch intent for package: ${context.packageName}")
        }
    }

    suspend fun changeLanguage(languageCode: String) {
        Log.d(TAG, "Changing language to: $languageCode")
        Log.d(TAG, "Language display name: ${SUPPORTED_LANGUAGES[languageCode]}")

        try {
            preferencesManager.updateLanguage(languageCode)
            Log.d(TAG, "Language preference saved successfully")

            applyLanguage(languageCode)
            Log.d(TAG, "Language applied, waiting before restart...")

            // Small delay to ensure preference is saved
            kotlinx.coroutines.delay(100)
            restartApp()
        } catch (e: Exception) {
            Log.e(TAG, "Error changing language", e)
        }
    }

    fun getCurrentLanguage(): String {
        val current = Locale.getDefault().language
        Log.d(TAG, "Current language: $current")
        return current
    }

    fun getLanguageDisplayName(languageCode: String): String {
        val displayName = SUPPORTED_LANGUAGES[languageCode] ?: languageCode.uppercase()
        Log.d(TAG, "Display name for $languageCode: $displayName")
        return displayName
    }
}
