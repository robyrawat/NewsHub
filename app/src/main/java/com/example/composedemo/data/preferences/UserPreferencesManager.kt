package com.example.composedemo.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private const val TAG = "UserPreferencesManager"
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val PAGE_SIZE = stringPreferencesKey("page_size")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val DARK_READING_MODE = booleanPreferencesKey("dark_reading_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SMART_RECOMMENDATIONS = booleanPreferencesKey("smart_recommendations")
        val PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        val OFFLINE_READING = booleanPreferencesKey("offline_reading")
        val ANALYTICS = booleanPreferencesKey("analytics")
        val VIEW_MODE = stringPreferencesKey("view_mode")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        val theme = preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
        Log.d(TAG, "Retrieved theme mode: $theme")
        theme
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        val lang = preferences[PreferencesKeys.LANGUAGE] ?: "en"
        Log.d(TAG, "Retrieved language: $lang")
        lang
    }

    val pageSize: Flow<String> = context.dataStore.data.map { preferences ->
        val size = preferences[PreferencesKeys.PAGE_SIZE] ?: "10"
        Log.d(TAG, "Retrieved page size: $size")
        size
    }

    val fontSize: Flow<String> = context.dataStore.data.map { preferences ->
        val size = preferences[PreferencesKeys.FONT_SIZE] ?: "medium"
        Log.d(TAG, "Retrieved font size: $size")
        size
    }

    val darkReadingMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_READING_MODE] ?: false
    }

    val hapticFeedback: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true
    }

    val smartRecommendations: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SMART_RECOMMENDATIONS] ?: true
    }

    val pushNotifications: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PUSH_NOTIFICATIONS] ?: true
    }

    val autoRefresh: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_REFRESH] ?: true
    }

    val offlineReading: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.OFFLINE_READING] ?: false
    }

    val analytics: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ANALYTICS] ?: true
    }

    val viewMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VIEW_MODE] ?: "LIST_VERTICAL"
    }

    suspend fun updateThemeMode(themeMode: String) {
        Log.d(TAG, "Updating theme mode to: $themeMode")
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME_MODE] = themeMode
            }
            Log.d(TAG, "Theme mode updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating theme mode", e)
        }
    }

    suspend fun updateLanguage(language: String) {
        Log.d(TAG, "Updating language to: $language")
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.LANGUAGE] = language
            }
            Log.d(TAG, "Language updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating language", e)
        }
    }

    suspend fun updatePageSize(pageSize: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGE_SIZE] = pageSize
        }
    }

    suspend fun updateFontSize(fontSize: String) {
        Log.d(TAG, "Updating font size to: $fontSize")
        try {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.FONT_SIZE] = fontSize
            }
            Log.d(TAG, "Font size updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating font size", e)
        }
    }

    suspend fun updateDarkReadingMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_READING_MODE] = enabled
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun updateSmartRecommendations(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SMART_RECOMMENDATIONS] = enabled
        }
    }

    suspend fun updatePushNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PUSH_NOTIFICATIONS] = enabled
        }
    }

    suspend fun updateAutoRefresh(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_REFRESH] = enabled
        }
    }

    suspend fun updateOfflineReading(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OFFLINE_READING] = enabled
        }
    }

    suspend fun updateAnalytics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANALYTICS] = enabled
        }
    }

    suspend fun updateViewMode(viewMode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIEW_MODE] = viewMode
        }
    }
}
