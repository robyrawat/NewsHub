package com.example.newshub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newshub.data.preferences.LanguageManager
import com.example.newshub.data.preferences.SettingsManager
import com.example.newshub.data.preferences.UserPreferencesManager
import com.example.newshub.util.DebugLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager,
    private val languageManager: LanguageManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Log.d(TAG, "SettingsViewModel initialized")
        loadSettings()
        loadReadingStats()
    }

    private fun loadSettings() {
        Log.d(TAG, "Loading settings from preferences...")
        viewModelScope.launch {
            _isLoading.value = true
            combine(
                preferencesManager.themeMode,
                preferencesManager.language,
                preferencesManager.pageSize,
                preferencesManager.fontSize,
                preferencesManager.darkReadingMode,
                preferencesManager.hapticFeedback,
                preferencesManager.smartRecommendations,
                preferencesManager.pushNotifications,
                preferencesManager.autoRefresh,
                preferencesManager.offlineReading,
                preferencesManager.analytics,
                preferencesManager.viewMode
            ) { flows ->
                val cacheSize = settingsManager.getCacheSize()
                val (articlesRead, readingStreak, favoriteCategory) = settingsManager.getReadingStats()

                val newState = SettingsUiState(
                    themeMode = flows[0] as String,
                    language = flows[1] as String,
                    pageSize = flows[2] as String,
                    fontSize = flows[3] as String,
                    isDarkReadingMode = flows[4] as Boolean,
                    hapticFeedback = flows[5] as Boolean,
                    smartRecommendations = flows[6] as Boolean,
                    pushNotifications = flows[7] as Boolean,
                    autoRefresh = flows[8] as Boolean,
                    offlineReading = flows[9] as Boolean,
                    analytics = flows[10] as Boolean,
                    viewMode = flows[11] as String,
                    cacheSize = (cacheSize / (1024 * 1024)).toInt(),
                    articlesRead = articlesRead,
                    readingStreak = readingStreak,
                    favoriteCategory = favoriteCategory
                )

                Log.d(TAG, "Settings loaded - Theme: ${newState.themeMode}, Language: ${newState.language}, Font: ${newState.fontSize}")
                newState
            }.collect { newState ->
                _settingsState.value = newState
                _isLoading.value = false
            }
        }
    }

    private fun loadReadingStats() {
        viewModelScope.launch {
            val (articlesRead, readingStreak, favoriteCategory) = settingsManager.getReadingStats()
            _settingsState.value = _settingsState.value.copy(
                articlesRead = articlesRead,
                readingStreak = readingStreak,
                favoriteCategory = favoriteCategory
            )
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            preferencesManager.updateThemeMode(theme)
            _settingsState.value = _settingsState.value.copy(themeMode = theme)
        }
    }

    fun updateLanguage(language: String) {
        DebugLogger.logInfo(TAG, "updateLanguage called with: $language")
        DebugLogger.logInfo(TAG, "Current language: ${_settingsState.value.language}")

        viewModelScope.launch {
            try {
                DebugLogger.logInfo(TAG, "Calling languageManager.changeLanguage($language)")
                languageManager.changeLanguage(language)
            } catch (e: Exception) {
                DebugLogger.logError(TAG, "Error changing language", e)
            }
        }
    }

    fun updateFontSize(fontSize: String) {
        DebugLogger.logInfo(TAG, "updateFontSize called with: $fontSize")
        DebugLogger.logInfo(TAG, "Current font size: ${_settingsState.value.fontSize}")

        viewModelScope.launch {
            try {
                preferencesManager.updateFontSize(fontSize)
                _settingsState.value = _settingsState.value.copy(fontSize = fontSize)
                DebugLogger.logInfo(TAG, "Font size updated successfully. New font size: ${_settingsState.value.fontSize}")
            } catch (e: Exception) {
                DebugLogger.logError(TAG, "Error updating font size", e)
            }
        }
    }

    fun updateViewMode(viewMode: String) {
        viewModelScope.launch {
            preferencesManager.updateViewMode(viewMode)
            _settingsState.value = _settingsState.value.copy(viewMode = viewMode)
        }
    }

    fun toggleDarkReadingMode() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.isDarkReadingMode
            preferencesManager.updateDarkReadingMode(newValue)
            _settingsState.value = _settingsState.value.copy(isDarkReadingMode = newValue)
        }
    }

    fun toggleHapticFeedback() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.hapticFeedback
            settingsManager.toggleHapticFeedback(newValue)
            _settingsState.value = _settingsState.value.copy(hapticFeedback = newValue)
        }
    }

    fun toggleSmartRecommendations() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.smartRecommendations
            settingsManager.toggleSmartRecommendations(newValue)
            _settingsState.value = _settingsState.value.copy(smartRecommendations = newValue)
        }
    }

    fun togglePushNotifications() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.pushNotifications
            settingsManager.togglePushNotifications(newValue)
            _settingsState.value = _settingsState.value.copy(pushNotifications = newValue)
        }
    }

    fun toggleAutoRefresh() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.autoRefresh
            settingsManager.toggleAutoRefresh(newValue)
            _settingsState.value = _settingsState.value.copy(autoRefresh = newValue)
        }
    }

    fun toggleOfflineReading() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.offlineReading
            settingsManager.toggleOfflineReading(newValue)
            _settingsState.value = _settingsState.value.copy(offlineReading = newValue)
        }
    }

    fun toggleAnalytics() {
        viewModelScope.launch {
            val newValue = !_settingsState.value.analytics
            settingsManager.toggleAnalytics(newValue)
            _settingsState.value = _settingsState.value.copy(analytics = newValue)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            val clearedBytes = settingsManager.clearCache()
            _settingsState.value = _settingsState.value.copy(cacheSize = 0)
        }
    }

    fun getSupportedLanguages(): Map<String, String> {
        return LanguageManager.SUPPORTED_LANGUAGES
    }

    fun getCurrentLanguageDisplayName(): String {
        return languageManager.getLanguageDisplayName(_settingsState.value.language)
    }

    fun getCacheSizeFormatted(): String {
        val bytes = _settingsState.value.cacheSize * 1024L * 1024L // Convert MB to bytes
        return settingsManager.formatBytes(bytes)
    }
}

data class SettingsUiState(
    val themeMode: String = "SYSTEM",
    val language: String = "en",
    val country: String = "us",
    val pageSize: String = "10",
    val fontSize: String = "medium",
    val viewMode: String = "LIST_VERTICAL",
    val isDarkReadingMode: Boolean = false,
    val hapticFeedback: Boolean = true,
    val smartRecommendations: Boolean = true,
    val pushNotifications: Boolean = true,
    val autoRefresh: Boolean = true,
    val offlineReading: Boolean = false,
    val analytics: Boolean = true,
    val cacheSize: Int = 45,
    val readingStreak: Int = 7,
    val articlesRead: Int = 156,
    val favoriteCategory: String = "Tech"
)
