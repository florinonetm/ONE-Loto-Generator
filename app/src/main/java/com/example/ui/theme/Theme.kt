package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DarkSlateBackground,
    primaryContainer = CyanAccentDark,
    onPrimaryContainer = CyanAccentGlow,
    secondary = CyanAccentDark,
    onSecondary = TextPrimary,
    tertiary = StarGold,
    onTertiary = DarkSlateBackground,
    background = DarkSlateBackground,
    onBackground = TextPrimary,
    surface = DarkSlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkSlateOutline
)

@Composable
fun OneLotoTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkSlateBackground.toArgb()
                window.navigationBarColor = DarkSlateBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
