package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a single drawing path with its properties
 *
 * @param groupId Optional group id; items with the same non-null groupId are grouped
 */
data class PathData(
    val id: String,
    val color: Color,
    val strokeWidth: Float,
    val path: List<Offset>,
    val groupId: String? = null
)
