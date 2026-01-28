package com.imagination.canvaspractice.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark theme color scheme
 */
private val darkColorScheme = SynapsesColorScheme(
    background = DarkColors.background,
    surface = DarkColors.surface,
    surfaceVariant = DarkColors.surfaceVariant,
    onBackground = DarkColors.onBackground,
    onSurface = DarkColors.onSurface,
    onSurfaceVariant = DarkColors.onSurfaceVariant,
    primary = DarkColors.primary,
    onPrimary = DarkColors.onPrimary,
    primaryContainer = DarkColors.primaryContainer,
    onPrimaryContainer = DarkColors.onPrimaryContainer,
    secondary = DarkColors.secondary,
    onSecondary = DarkColors.onSecondary,
    secondaryContainer = DarkColors.secondaryContainer,
    onSecondaryContainer = DarkColors.onSecondaryContainer,
    outline = DarkColors.outline,
    outlineVariant = DarkColors.outlineVariant,
    info = DarkColors.info,
    success = DarkColors.success,
    warning = DarkColors.warning,
    error = DarkColors.error,
    onError = DarkColors.onError,
    errorContainer = DarkColors.errorContainer,
    onErrorContainer = DarkColors.onErrorContainer
)

/**
 * Light theme color scheme
 */
private val lightColorScheme = SynapsesColorScheme(
    background = LightColors.background,
    surface = LightColors.surface,
    surfaceVariant = LightColors.surfaceVariant,
    onBackground = LightColors.onBackground,
    onSurface = LightColors.onSurface,
    onSurfaceVariant = LightColors.onSurfaceVariant,
    primary = LightColors.primary,
    onPrimary = LightColors.onPrimary,
    primaryContainer = LightColors.primaryContainer,
    onPrimaryContainer = LightColors.onPrimaryContainer,
    secondary = LightColors.secondary,
    onSecondary = LightColors.onSecondary,
    secondaryContainer = LightColors.secondaryContainer,
    onSecondaryContainer = LightColors.onSecondaryContainer,
    outline = LightColors.outline,
    outlineVariant = LightColors.outlineVariant,
    info = LightColors.info,
    success = LightColors.success,
    warning = LightColors.warning,
    error = LightColors.error,
    onError = LightColors.onError,
    errorContainer = LightColors.errorContainer,
    onErrorContainer = LightColors.onErrorContainer
)

/**
 * Main theme composable for the Synapses app.
 * Provides color scheme, typography, shapes, and spacing throughout the composition tree.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to enable dynamic colors (Android 12+). Currently not implemented.
 * @param content The composable content that will receive the theme.
 */
@Composable
fun CanvasPracticeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    // Add primary status bar color from chosen color scheme.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusBarColor = Color.Transparent
            // Set status bar color
            @Suppress("DEPRECATION")
            window.statusBarColor = statusBarColor.toArgb()
            // Determine if light icons should be used based on color brightness
            val isLightStatusBar = statusBarColor.luminance() > 0.5f
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightStatusBar
        }
    }

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides typography,
        LocalShape provides shape,
        LocalSpacing provides spacing,
        content = content
    )
}

/**
 * Convenience object for accessing theme values.
 * Use this object to access the current theme values in composable.
 */
object CanvasPracticeTheme {
    val colorScheme: SynapsesColorScheme
        @Composable get() = LocalColorScheme.current

    val typography: SynapsesTypography
        @Composable get() = LocalTypography.current

    val shape: SynapsesShape
        @Composable get() = LocalShape.current

    val spacing: SynapsesSpacing
        @Composable get() = LocalSpacing.current
}