package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PersianGulfColorScheme = darkColorScheme(
    primary = GulfCyan,
    onPrimary = GulfNavyDeep,
    primaryContainer = GulfNavyCard,
    onPrimaryContainer = GulfCyan,
    secondary = GulfGold,
    onSecondary = GulfNavyDeep,
    secondaryContainer = GulfNavyBorder,
    onSecondaryContainer = GulfGold,
    tertiary = GulfTurquoise,
    onTertiary = Color.White,
    background = GulfNavyDeep,
    onBackground = TextPrimary,
    surface = GulfNavyDark,
    onSurface = TextPrimary,
    surfaceVariant = GulfNavySurface,
    onSurfaceVariant = TextSecondary,
    outline = GulfNavyBorder,
    outlineVariant = GulfGold.copy(alpha = 0.3f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PersianGulfColorScheme,
        typography = Typography,
        content = content
    )
}
