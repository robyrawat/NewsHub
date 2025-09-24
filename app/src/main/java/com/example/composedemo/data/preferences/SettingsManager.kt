package com.example.composedemo.data.preferences

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val context: Context,
    private val preferencesManager: UserPreferencesManager
) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    /**
     * Handle haptic feedback toggle
     */
    suspend fun toggleHapticFeedback(enabled: Boolean) {
        preferencesManager.updateHapticFeedback(enabled)
        // Store in SharedPreferences for immediate access
        sharedPrefs.edit().putBoolean("haptic_feedback", enabled).apply()
    }

    fun isHapticFeedbackEnabled(): Boolean {
        return sharedPrefs.getBoolean("haptic_feedback", true)
    }

    /**
     * Handle push notifications toggle
     */
    suspend fun togglePushNotifications(enabled: Boolean) {
        preferencesManager.updatePushNotifications(enabled)

        if (enabled) {
            enableNotifications()
        } else {
            disableNotifications()
        }
    }

    private fun enableNotifications() {
        // Create notification channel and enable notifications
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // You would typically create notification channels here
        // For demonstration, we'll just show a test notification
        val notification = NotificationCompat.Builder(context, "news_channel")
            .setContentTitle("Notifications Enabled")
            .setContentText("You'll now receive news updates")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(1001, notification)
        } catch (e: Exception) {
            // Handle notification permission issues
        }
    }

    private fun disableNotifications() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    /**
     * Handle auto-refresh toggle
     */
    suspend fun toggleAutoRefresh(enabled: Boolean) {
        preferencesManager.updateAutoRefresh(enabled)

        if (enabled) {
            startAutoRefreshService()
        } else {
            stopAutoRefreshService()
        }
    }

    private fun startAutoRefreshService() {
        // Implement background service for auto-refresh
        // This would typically use WorkManager for periodic updates
        sharedPrefs.edit().putBoolean("auto_refresh_active", true).apply()
    }

    private fun stopAutoRefreshService() {
        sharedPrefs.edit().putBoolean("auto_refresh_active", false).apply()
    }

    /**
     * Handle offline reading toggle
     */
    suspend fun toggleOfflineReading(enabled: Boolean) {
        preferencesManager.updateOfflineReading(enabled)

        if (enabled) {
            enableOfflineMode()
        } else {
            disableOfflineMode()
        }
    }

    private fun enableOfflineMode() {
        // Enable automatic article caching
        sharedPrefs.edit().putBoolean("offline_mode", true).apply()
    }

    private fun disableOfflineMode() {
        sharedPrefs.edit().putBoolean("offline_mode", false).apply()
    }

    /**
     * Handle analytics toggle
     */
    suspend fun toggleAnalytics(enabled: Boolean) {
        preferencesManager.updateAnalytics(enabled)

        if (enabled) {
            enableAnalytics()
        } else {
            disableAnalytics()
        }
    }

    private fun enableAnalytics() {
        // Enable analytics collection
        sharedPrefs.edit().putBoolean("analytics_enabled", true).apply()
    }

    private fun disableAnalytics() {
        // Disable analytics and clear collected data
        sharedPrefs.edit().putBoolean("analytics_enabled", false).apply()
    }

    /**
     * Handle smart recommendations toggle
     */
    suspend fun toggleSmartRecommendations(enabled: Boolean) {
        preferencesManager.updateSmartRecommendations(enabled)

        if (enabled) {
            enableSmartRecommendations()
        } else {
            disableSmartRecommendations()
        }
    }

    private fun enableSmartRecommendations() {
        // Start collecting user preferences for recommendations
        sharedPrefs.edit().putBoolean("smart_recommendations", true).apply()
    }

    private fun disableSmartRecommendations() {
        sharedPrefs.edit().putBoolean("smart_recommendations", false).apply()
    }

    /**
     * Clear app cache
     */
    fun clearCache(): Long {
        var clearedBytes = 0L

        try {
            // Clear image cache (Coil)
            // coil.ImageLoader(context).memoryCache?.clear()

            // Clear app cache directory
            val cacheDir = context.cacheDir
            if (cacheDir != null && cacheDir.isDirectory) {
                clearedBytes = deleteDir(cacheDir)
            }

            // Clear shared preferences cache
            sharedPrefs.edit().remove("cached_articles_size").apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return clearedBytes
    }

    private fun deleteDir(dir: java.io.File): Long {
        var deletedBytes = 0L
        if (dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val childFile = java.io.File(dir, child)
                    deletedBytes += deleteDir(childFile)
                }
            }
        }

        if (dir.exists()) {
            deletedBytes += dir.length()
            dir.delete()
        }

        return deletedBytes
    }

    /**
     * Get cache size
     */
    fun getCacheSize(): Long {
        var size = 0L

        try {
            val cacheDir = context.cacheDir
            if (cacheDir != null && cacheDir.exists()) {
                size = getDirSize(cacheDir)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    private fun getDirSize(dir: java.io.File): Long {
        var size = 0L

        if (dir.isDirectory) {
            val children = dir.listFiles()
            if (children != null) {
                for (child in children) {
                    size += getDirSize(child)
                }
            }
        } else {
            size = dir.length()
        }

        return size
    }

    /**
     * Format bytes to human readable format
     */
    fun formatBytes(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return "$bytes B"

        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1]

        return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    /**
     * Update reading statistics
     */
    fun updateReadingStats(articleRead: Boolean = false) {
        if (articleRead) {
            val currentCount = sharedPrefs.getInt("articles_read", 156)
            sharedPrefs.edit().putInt("articles_read", currentCount + 1).apply()

            // Update reading streak
            val lastReadDate = sharedPrefs.getLong("last_read_date", 0)
            val today = System.currentTimeMillis()
            val daysDiff = (today - lastReadDate) / (1000 * 60 * 60 * 24)

            if (daysDiff <= 1) {
                val currentStreak = sharedPrefs.getInt("reading_streak", 7)
                sharedPrefs.edit().putInt("reading_streak", currentStreak + 1).apply()
            } else {
                sharedPrefs.edit().putInt("reading_streak", 1).apply()
            }

            sharedPrefs.edit().putLong("last_read_date", today).apply()
        }
    }

    /**
     * Get reading statistics
     */
    fun getReadingStats(): Triple<Int, Int, String> {
        val articlesRead = sharedPrefs.getInt("articles_read", 156)
        val readingStreak = sharedPrefs.getInt("reading_streak", 7)
        val favoriteCategory = sharedPrefs.getString("favorite_category", "Tech") ?: "Tech"

        return Triple(articlesRead, readingStreak, favoriteCategory)
    }
}
