package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a text element on the canvas
 * 
 * @param id Unique identifier for this text element
 * @param text The text content
 * @param position The position where the text is placed
 * @param color The color of the text
 * @param fontSize The size of the text in pixels
 */
data class TextData(
    val id: String,
    val text: String,
    val position: Offset,
    val color: Color,
    val fontSize: Float
)
