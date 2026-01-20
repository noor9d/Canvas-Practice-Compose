package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * Represents a shape element on the canvas
 * 
 * @param id Unique identifier for this shape
 * @param type The type of shape (rectangle, circle, etc.)
 * @param startPosition The starting position of the shape
 * @param endPosition The ending position of the shape
 * @param color The color of the shape
 * @param strokeWidth The width of the stroke in pixels
 * @param isFilled Whether the shape should be filled or just outlined
 */
data class ShapeData(
    val id: String,
    val type: ShapeType,
    val startPosition: Offset,
    val endPosition: Offset,
    val color: Color,
    val strokeWidth: Float,
    val isFilled: Boolean = false
) {
    /**
     * Calculates the size of the shape based on start and end positions
     */
    val size: Size
        get() = Size(
            width = kotlin.math.abs(endPosition.x - startPosition.x),
            height = kotlin.math.abs(endPosition.y - startPosition.y)
        )
    
    /**
     * Calculates the top-left position for drawing the shape
     */
    val topLeft: Offset
        get() = Offset(
            x = kotlin.math.min(startPosition.x, endPosition.x),
            y = kotlin.math.min(startPosition.y, endPosition.y)
        )
}
