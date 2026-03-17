package com.habitonix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    secondary = Color(0xFF2B2B2B),
    tertiary = OrangeStreak,
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimary,
    secondary = Color(0xFFE6E6E6),
    tertiary = OrangeStreak,
)

@Composable
fun HabitonixTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}

