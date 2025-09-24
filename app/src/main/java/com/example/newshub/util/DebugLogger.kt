package com.example.newshub.util

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*


object DebugLogger {
    private const val TAG = "DebugLogger"
    private val debugLogs = mutableListOf<DebugLog>()

    data class DebugLog(
        val timestamp: String,
        val tag: String,
        val message: String,
        val level: LogLevel
    )

    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    fun logDebug(tag: String, message: String) {
        val log = DebugLog(getCurrentTime(), tag, message, LogLevel.DEBUG)
        debugLogs.add(0, log) // Add to beginning
        if (debugLogs.size > 100) debugLogs.removeAt(100) // Keep only last 100
        Log.d(tag, message)
    }

    fun logInfo(tag: String, message: String) {
        val log = DebugLog(getCurrentTime(), tag, message, LogLevel.INFO)
        debugLogs.add(0, log)
        if (debugLogs.size > 100) debugLogs.removeAt(100)
        Log.i(tag, message)
    }

    fun logWarn(tag: String, message: String) {
        val log = DebugLog(getCurrentTime(), tag, message, LogLevel.WARN)
        debugLogs.add(0, log)
        if (debugLogs.size > 100) debugLogs.removeAt(100)
        Log.w(tag, message)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        val log = DebugLog(getCurrentTime(), tag, message, LogLevel.ERROR)
        debugLogs.add(0, log)
        if (debugLogs.size > 100) debugLogs.removeAt(100)
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun getLogs(): List<DebugLog> = debugLogs.toList()

    fun clearLogs() {
        debugLogs.clear()
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
    }
}

@Composable
fun DebugLogViewer(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🐛 Debug Logs",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row {
                            TextButton(onClick = { DebugLogger.clearLogs() }) {
                                Text("Clear")
                            }
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    LazyColumn {
                        items(DebugLogger.getLogs()) { log ->
                            LogItem(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogItem(log: DebugLogger.DebugLog) {
    val orangeColor = Color(0xFFFFA500)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (log.level) {
                DebugLogger.LogLevel.ERROR -> Color.Red.copy(alpha = 0.1f)
                DebugLogger.LogLevel.WARN -> orangeColor.copy(alpha = 0.1f)
                DebugLogger.LogLevel.INFO -> Color.Blue.copy(alpha = 0.1f)
                DebugLogger.LogLevel.DEBUG -> Color.Gray.copy(alpha = 0.05f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.tag,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = log.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun DebugFab(
    onShowLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onShowLogs,
        modifier = modifier,
        containerColor = Color.Red.copy(alpha = 0.8f)
    ) {
        Icon(
            imageVector = Icons.Default.BugReport,
            contentDescription = "Debug Logs",
            tint = Color.White
        )
    }
}
