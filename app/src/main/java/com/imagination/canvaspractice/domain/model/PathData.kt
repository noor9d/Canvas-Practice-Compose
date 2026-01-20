package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a single drawing path with its properties
 * 
 * @param id Unique identifier for this path
 * @param color The color used to draw this path
 * @param strokeWidth The width of the stroke in pixels
 * @param path List of offset points that make up this path
 */
data class PathData(
    val id: String,
    val color: Color,
    val strokeWidth: Float,
    val path: List<Offset>
)
