package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * Represents a shape element on the canvas
 *
 * @param groupId Optional group id; items with the same non-null groupId are grouped
 */
data class ShapeData(
    val id: String,
    val type: ShapeType,
    val startPosition: Offset,
    val endPosition: Offset,
    val color: Color,
    val strokeWidth: Float,
    val isFilled: Boolean = false,
    val groupId: String? = null
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
