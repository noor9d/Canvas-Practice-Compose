package com.imagination.canvaspractice.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * Color scheme for the Synapses design system.
 * Uses Material Design semantic color naming for consistency.
 */
data class SynapsesColorScheme(
    // Background colors
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    
    // Text/content colors
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    
    // Primary colors
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    
    // Secondary colors
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    
    // Outline/border colors
    val outline: Color,
    val outlineVariant: Color,
    
    // Semantic colors (same in both themes)
    val info: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color
)

/**
 * Typography system for the Synapses design system.
 * Defines text styles with consistent font family, weights, and sizes.
 */
data class SynapsesTypography(
    val display: TextStyle,
    val activityTitle: TextStyle,
    val dialogHeading: TextStyle,
    val moduleHeading: TextStyle,
    val listingText: TextStyle,
    val featureButton: TextStyle,
    val labelNav: TextStyle,
    val body: TextStyle,
    val infoText: TextStyle,
    val categoryText: TextStyle,
    val full: TextStyle,
    val half: TextStyle,
    val text: TextStyle
)

/**
 * Shape system for the Synapses design system.
 * Defines rounded corner shapes for various UI components.
 */
data class SynapsesShape(
    val rounded: Shape,
    val dialog: Shape,
    val tile: Shape,
    val smallTile: Shape,
    val button: Shape,
    val ad: Shape
)

/**
 * Spacing system for the Synapses design system.
 * Provides consistent spacing tokens for padding and margins.
 */
data class SynapsesSpacing(
    val large: Dp,
    val medium: Dp,
    val normal: Dp,
    val small: Dp,
    val extraSmall: Dp = small / 2,
    val extraLarge: Dp = large * 1
)

/**
 * CompositionLocal for providing the current color scheme throughout the composition tree.
 */
val LocalColorScheme = staticCompositionLocalOf {
    SynapsesColorScheme(
        background = Color.Unspecified,
        surface = Color.Unspecified,
        surfaceVariant = Color.Unspecified,
        onBackground = Color.Unspecified,
        onSurface = Color.Unspecified,
        onSurfaceVariant = Color.Unspecified,
        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,
        primaryContainer = Color.Unspecified,
        onPrimaryContainer = Color.Unspecified,
        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified,
        secondaryContainer = Color.Unspecified,
        onSecondaryContainer = Color.Unspecified,
        outline = Color.Unspecified,
        outlineVariant = Color.Unspecified,
        info = Color.Unspecified,
        success = Color.Unspecified,
        warning = Color.Unspecified,
        error = Color.Unspecified,
        onError = Color.Unspecified,
        errorContainer = Color.Unspecified,
        onErrorContainer = Color.Unspecified
    )
}

/**
 * CompositionLocal for providing the current typography system throughout the composition tree.
 */
val LocalTypography = staticCompositionLocalOf {
    SynapsesTypography(
        display = TextStyle.Default,
        activityTitle = TextStyle.Default,
        dialogHeading = TextStyle.Default,
        moduleHeading = TextStyle.Default,
        listingText = TextStyle.Default,
        featureButton = TextStyle.Default,
        labelNav = TextStyle.Default,
        body = TextStyle.Default,
        infoText = TextStyle.Default,
        categoryText = TextStyle.Default,
        full = TextStyle.Default,
        half = TextStyle.Default,
        text = TextStyle.Default
    )
}

/**
 * CompositionLocal for providing the current shape system throughout the composition tree.
 */
val LocalShape = staticCompositionLocalOf {
    SynapsesShape(
        rounded = RectangleShape,
        dialog = RectangleShape,
        tile = RectangleShape,
        smallTile = RectangleShape,
        button = RectangleShape,
        ad = RectangleShape
    )
}

/**
 * CompositionLocal for providing the current spacing system throughout the composition tree.
 */
val LocalSpacing = staticCompositionLocalOf {
    SynapsesSpacing(
        large = Dp.Unspecified,
        medium = Dp.Unspecified,
        normal = Dp.Unspecified,
        small = Dp.Unspecified
    )
}