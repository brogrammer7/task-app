package com.example.taskapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * A single fixed scheme, dark only.
 *
 * Dynamic color is deliberately off: this palette *is* the product's identity, and
 * letting Material You derive it from the user's wallpaper would discard it on every
 * Android 12+ device. There is no light variant for the same reason — emitted light
 * needs an unlit ground to read as light at all.
 */
private val NeonColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = VoidNavy,
    primaryContainer = PanelNavyLift,
    onPrimaryContainer = NeonCyan,

    secondary = SignalAmber,
    onSecondary = VoidNavy,
    tertiary = SignalAmber,
    onTertiary = VoidNavy,

    error = PlasmaMagenta,
    onError = VoidNavy,
    errorContainer = MagentaWell,
    onErrorContainer = PlasmaMagenta,

    background = VoidNavy,
    onBackground = FrostWhite,
    surface = PanelNavy,
    onSurface = FrostWhite,
    surfaceVariant = PanelNavyLift,
    onSurfaceVariant = DimSlate,
    outline = EdgeNavy,
    outlineVariant = EdgeNavy
)

@Composable
fun TaskAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // The scheme is always dark, so the system bar icons always need to be light.
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = NeonColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
