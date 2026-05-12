package com.example.antharjalawatch.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light color scheme using our custom blue/water palette
private val LightColorScheme = lightColorScheme(
    primary          = Blue40,
    onPrimary        = White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary        = Teal40,
    onSecondary      = White,
    secondaryContainer = Teal80,

    background       = SurfaceLight,
    onBackground     = Gray10,

    surface          = CardWhite,
    onSurface        = Gray10,

    surfaceVariant   = Blue95,
    onSurfaceVariant = Gray20,

    error            = ErrorRed,
    onError          = White,
    errorContainer   = ErrorRedLight,
)

private val DarkColorScheme = darkColorScheme(
    primary          = Blue80,
    onPrimary        = Blue20,
    primaryContainer = Blue40,
    background       = Blue10,
    surface          = Gray10,
)

/**
 * AntharJalaTheme wraps the entire app.
 * Place this in MainActivity around your NavHost.
 */
@Composable
fun AntharJalaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}
