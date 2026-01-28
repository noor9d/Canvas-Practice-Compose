package com.synapses.presentation.dashboard.model

/**
 * Represents a Board in the dashboard.
 * This will be replaced with a Room entity in the future.
 */
data class Board(
    val id: Int,
    val title: String,
    val thumbnailUrl: String? = null // For future use with actual thumbnails
)
