package com.imagination.canvaspractice.domain.constants

import androidx.compose.ui.graphics.Color

/**
 * Constants used for drawing functionality
 */
object DrawingConstants {
    /**
     * Default stroke width for drawing paths
     */
    const val DEFAULT_STROKE_WIDTH = 8f
    
    /**
     * Default font size for text elements
     */
    const val DEFAULT_FONT_SIZE = 24f

    // Constants for grid and zoom
    const val GRID_SIZE = 200f
    const val MIN_SCALE = 0.1f
    const val MAX_SCALE = 10f
    val GRID_COLOR = Color.LightGray.copy(alpha = 0.5f)
    
    /**
     * Minimum distance threshold for path smoothing (in pixels)
     * Points closer than this will be smoothed together
     */
    const val SMOOTHNESS_THRESHOLD = 5f
    
    /**
     * Available colors for drawing
     */
    val AVAILABLE_COLORS = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
    )
    
    /**
     * Default drawing color
     */
    val DEFAULT_COLOR = Color.Black
}
