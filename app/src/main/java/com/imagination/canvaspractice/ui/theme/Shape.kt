package com.imagination.canvaspractice.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Shape system for the Synapses design system.
 * Defines consistent rounded corner shapes for UI components.
 */
val shape = SynapsesShape(
    rounded = RoundedCornerShape(100.dp),
    dialog = RoundedCornerShape(25.dp),
    tile = RoundedCornerShape(22.dp),
    smallTile = RoundedCornerShape(16.dp),
    button = RoundedCornerShape(12.dp),
    ad = RoundedCornerShape(8.dp)
)