package com.example.composedemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Base Typography
private val BaseTypography = Typography()

// Font size configurations
object FontSizes {
    val small = FontSizeConfig(
        displayLarge = 48.sp,
        displayMedium = 40.sp,
        displaySmall = 32.sp,
        headlineLarge = 28.sp,
        headlineMedium = 24.sp,
        headlineSmall = 20.sp,
        titleLarge = 18.sp,
        titleMedium = 14.sp,
        titleSmall = 12.sp,
        bodyLarge = 14.sp,
        bodyMedium = 12.sp,
        bodySmall = 10.sp,
        labelLarge = 12.sp,
        labelMedium = 10.sp,
        labelSmall = 9.sp
    )

    val medium = FontSizeConfig(
        displayLarge = 57.sp,
        displayMedium = 45.sp,
        displaySmall = 36.sp,
        headlineLarge = 32.sp,
        headlineMedium = 28.sp,
        headlineSmall = 24.sp,
        titleLarge = 22.sp,
        titleMedium = 16.sp,
        titleSmall = 14.sp,
        bodyLarge = 16.sp,
        bodyMedium = 14.sp,
        bodySmall = 12.sp,
        labelLarge = 14.sp,
        labelMedium = 12.sp,
        labelSmall = 11.sp
    )

    val large = FontSizeConfig(
        displayLarge = 64.sp,
        displayMedium = 52.sp,
        displaySmall = 42.sp,
        headlineLarge = 38.sp,
        headlineMedium = 32.sp,
        headlineSmall = 28.sp,
        titleLarge = 26.sp,
        titleMedium = 18.sp,
        titleSmall = 16.sp,
        bodyLarge = 18.sp,
        bodyMedium = 16.sp,
        bodySmall = 14.sp,
        labelLarge = 16.sp,
        labelMedium = 14.sp,
        labelSmall = 13.sp
    )
}

data class FontSizeConfig(
    val displayLarge: androidx.compose.ui.unit.TextUnit,
    val displayMedium: androidx.compose.ui.unit.TextUnit,
    val displaySmall: androidx.compose.ui.unit.TextUnit,
    val headlineLarge: androidx.compose.ui.unit.TextUnit,
    val headlineMedium: androidx.compose.ui.unit.TextUnit,
    val headlineSmall: androidx.compose.ui.unit.TextUnit,
    val titleLarge: androidx.compose.ui.unit.TextUnit,
    val titleMedium: androidx.compose.ui.unit.TextUnit,
    val titleSmall: androidx.compose.ui.unit.TextUnit,
    val bodyLarge: androidx.compose.ui.unit.TextUnit,
    val bodyMedium: androidx.compose.ui.unit.TextUnit,
    val bodySmall: androidx.compose.ui.unit.TextUnit,
    val labelLarge: androidx.compose.ui.unit.TextUnit,
    val labelMedium: androidx.compose.ui.unit.TextUnit,
    val labelSmall: androidx.compose.ui.unit.TextUnit
)

@Composable
fun getTypographyForFontSize(fontSizeMode: String): Typography {
    val fontConfig = when (fontSizeMode) {
        "small" -> FontSizes.small
        "large" -> FontSizes.large
        else -> FontSizes.medium // default
    }

    return Typography(
        displayLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.displayLarge,
            lineHeight = (fontConfig.displayLarge.value * 1.2).sp,
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.displayMedium,
            lineHeight = (fontConfig.displayMedium.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        displaySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.displaySmall,
            lineHeight = (fontConfig.displaySmall.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        headlineLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.headlineLarge,
            lineHeight = (fontConfig.headlineLarge.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        headlineMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.headlineMedium,
            lineHeight = (fontConfig.headlineMedium.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        headlineSmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.headlineSmall,
            lineHeight = (fontConfig.headlineSmall.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.titleLarge,
            lineHeight = (fontConfig.titleLarge.value * 1.2).sp,
            letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = fontConfig.titleMedium,
            lineHeight = (fontConfig.titleMedium.value * 1.2).sp,
            letterSpacing = 0.15.sp,
        ),
        titleSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = fontConfig.titleSmall,
            lineHeight = (fontConfig.titleSmall.value * 1.2).sp,
            letterSpacing = 0.1.sp,
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.bodyLarge,
            lineHeight = (fontConfig.bodyLarge.value * 1.5).sp,
            letterSpacing = 0.15.sp,
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.bodyMedium,
            lineHeight = (fontConfig.bodyMedium.value * 1.5).sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = fontConfig.bodySmall,
            lineHeight = (fontConfig.bodySmall.value * 1.5).sp,
            letterSpacing = 0.4.sp,
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = fontConfig.labelLarge,
            lineHeight = (fontConfig.labelLarge.value * 1.2).sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = fontConfig.labelMedium,
            lineHeight = (fontConfig.labelMedium.value * 1.2).sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = fontConfig.labelSmall,
            lineHeight = (fontConfig.labelSmall.value * 1.2).sp,
            letterSpacing = 0.5.sp,
        )
    )
}
