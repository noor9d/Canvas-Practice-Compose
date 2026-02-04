package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a text element on the canvas
 *
 * @param groupId Optional group id; items with the same non-null groupId are grouped
 */
data class TextData(
    val id: String,
    val text: String,
    val position: Offset,
    val color: Color,
    val fontSize: Float,
    val groupId: String? = null
)
