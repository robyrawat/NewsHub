package com.example.composedemo.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.composedemo.data.preferences.LanguageManager
import com.example.composedemo.presentation.ui.components.CommonTopBar
import com.example.composedemo.presentation.viewmodel.SettingsViewModel
import com.example.composedemo.util.DebugLogViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }

    var showDebugLogs by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Settings",
                navController = navController,
                showBackButton = false,
                actions = {
                    // Debug FAB for testing
                    IconButton(onClick = { showDebugLogs = true }) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Debug Logs",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(onClick = {
                        // Export settings or show info
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "App Info",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserProfileCard(
                    readingStreak = settingsState.readingStreak,
                    articlesRead = settingsState.articlesRead,
                    favoriteCategory = settingsState.favoriteCategory
                )
            }

            item {
                SettingsSection(title = "🎨 Appearance") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = getCurrentThemeText(settingsState.themeMode),
                        onClick = {
                            showThemeDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.FormatSize,
                        title = "Font Size",
                        subtitle = getCurrentFontSizeText(settingsState.fontSize),
                        onClick = {
                            showFontSizeDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.ColorLens,
                        title = "Reading Mode",
                        subtitle = if (settingsState.isDarkReadingMode) "Dark" else "Light",
                        onClick = {
                            settingsViewModel.toggleDarkReadingMode()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        trailing = {
                            Switch(
                                checked = settingsState.isDarkReadingMode,
                                onCheckedChange = { settingsViewModel.toggleDarkReadingMode() }
                            )
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "🌍 Content & Language") {
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = getCurrentLanguageText(settingsState.language),
                        onClick = {
                            showLanguageDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Public,
                        title = "Country/Region",
                        subtitle = getCurrentCountryText(settingsState.country),
                        onClick = { /* Show country dialog */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.FilterList,
                        title = "Content Filter",
                        subtitle = "Safe for work",
                        onClick = { /* Show content filter */ }
                    )
                }
            }

            item {
                SettingsSection(title = "📱 Experience") {
                    SettingsItem(
                        icon = Icons.Default.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Feel the interactions",
                        trailing = {
                            Switch(
                                checked = settingsState.hapticFeedback,
                                onCheckedChange = { settingsViewModel.toggleHapticFeedback() }
                            )
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "Smart Recommendations",
                        subtitle = "Personalized news feed",
                        trailing = {
                            Switch(
                                checked = settingsState.smartRecommendations,
                                onCheckedChange = { settingsViewModel.toggleSmartRecommendations() }
                            )
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Breaking news alerts",
                        trailing = {
                            Switch(
                                checked = settingsState.pushNotifications,
                                onCheckedChange = { settingsViewModel.togglePushNotifications() }
                            )
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Wifi,
                        title = "Auto-refresh",
                        subtitle = "Update news automatically",
                        trailing = {
                            Switch(
                                checked = settingsState.autoRefresh,
                                onCheckedChange = { settingsViewModel.toggleAutoRefresh() }
                            )
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "📊 Data & Privacy") {
                    SettingsItem(
                        icon = Icons.Default.CloudDownload,
                        title = "Offline Reading",
                        subtitle = "Download articles for offline",
                        trailing = {
                            Switch(
                                checked = settingsState.offlineReading,
                                onCheckedChange = { settingsViewModel.toggleOfflineReading() }
                            )
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Analytics,
                        title = "Usage Analytics",
                        subtitle = "Help improve the app",
                        trailing = {
                            Switch(
                                checked = settingsState.analytics,
                                onCheckedChange = { settingsViewModel.toggleAnalytics() }
                            )
                        }
                    )

                    SettingsItem(
                        icon = Icons.Default.Storage,
                        title = "Clear Cache",
                        subtitle = "${settingsState.cacheSize}MB used",
                        onClick = {
                            settingsViewModel.clearCache()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "ℹ️ About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0 (Latest)",
                        onClick = { /* Show version info */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.Star,
                        title = "Rate App",
                        subtitle = "Love NewsHub? Rate us!",
                        onClick = { /* Open Play Store */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.BugReport,
                        title = "Report Bug",
                        subtitle = "Help us improve",
                        onClick = { /* Open bug report */ }
                    )

                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "How we protect your data",
                        onClick = { /* Open privacy policy */ }
                    )
                }
            }
        }
    }

    // Theme selection dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeOption("System Default", "SYSTEM", settingsState.themeMode) {
                        settingsViewModel.updateTheme(it)
                        showThemeDialog = false
                    }
                    ThemeOption("Light", "LIGHT", settingsState.themeMode) {
                        settingsViewModel.updateTheme(it)
                        showThemeDialog = false
                    }
                    ThemeOption("Dark", "DARK", settingsState.themeMode) {
                        settingsViewModel.updateTheme(it)
                        showThemeDialog = false
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Language selection dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Choose Language") },
            text = {
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    items(settingsViewModel.getSupportedLanguages().toList()) { (code, name) ->
                        LanguageOption(
                            code = code,
                            name = name,
                            isSelected = code == settingsState.language,
                            onSelected = {
                                settingsViewModel.updateLanguage(code)
                                showLanguageDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Font size dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text("Font Size") },
            text = {
                Column {
                    FontSizeOption("Small", "small", settingsState.fontSize) {
                        settingsViewModel.updateFontSize(it)
                        showFontSizeDialog = false
                    }
                    FontSizeOption("Medium", "medium", settingsState.fontSize) {
                        settingsViewModel.updateFontSize(it)
                        showFontSizeDialog = false
                    }
                    FontSizeOption("Large", "large", settingsState.fontSize) {
                        settingsViewModel.updateFontSize(it)
                        showFontSizeDialog = false
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Debug logs viewer
    DebugLogViewer(
        isVisible = showDebugLogs,
        onDismiss = { showDebugLogs = false }
    )
}

@Composable
fun UserProfileCard(
    readingStreak: Int,
    articlesRead: Int,
    favoriteCategory: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📚 Your Reading Stats",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Keep up the great reading habit!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "$readingStreak",
                    label = "Day Streak"
                )

                StatCard(
                    icon = Icons.Default.Article,
                    value = "$articlesRead",
                    label = "Articles Read"
                )

                StatCard(
                    icon = Icons.Default.Category,
                    value = favoriteCategory,
                    label = "Favorite Topic"
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        trailing?.invoke() ?: run {
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    text: String,
    themeMode: String,
    currentTheme: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(themeMode) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = currentTheme == themeMode,
            onClick = { onSelected(themeMode) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun LanguageOption(
    code: String,
    name: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = code.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FontSizeOption(
    text: String,
    size: String,
    currentSize: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(size) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = currentSize == size,
            onClick = { onSelected(size) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = when (size) {
                "small" -> MaterialTheme.typography.bodyMedium
                "medium" -> MaterialTheme.typography.bodyLarge
                "large" -> MaterialTheme.typography.headlineSmall
                else -> MaterialTheme.typography.bodyLarge
            }
        )
    }
}

// Helper functions for display text
private fun getCurrentThemeText(theme: String): String {
    return when (theme) {
        "LIGHT" -> "Light"
        "DARK" -> "Dark"
        "SYSTEM" -> "System Default"
        else -> "System Default"
    }
}

private fun getCurrentLanguageText(languageCode: String): String {
    return LanguageManager.SUPPORTED_LANGUAGES[languageCode] ?: "English"
}

private fun getCurrentFontSizeText(fontSize: String): String {
    return when (fontSize) {
        "small" -> "Small"
        "medium" -> "Medium"
        "large" -> "Large"
        else -> "Medium"
    }
}

private fun getCurrentCountryText(country: String): String {
    return when (country) {
        "us" -> "United States"
        "gb" -> "United Kingdom"
        "ca" -> "Canada"
        "au" -> "Australia"
        else -> "United States"
    }
}
