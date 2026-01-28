package com.synapses.presentation.dashboard.model

/**
 * Represents a Note in the dashboard.
 * This will be replaced with a Room entity in the future.
 */
data class Note(
    val id: Int,
    val title: String,
    val thumbnailUrl: String? = null // For future use with actual thumbnails
)
