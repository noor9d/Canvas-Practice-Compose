package com.imagination.canvaspractice.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.imagination.canvaspractice.R

/**
 * FigTree font family for the Synapses design system.
 * Includes all font weights: Regular, Medium, SemiBold, and Bold.
 */
val FigTree = FontFamily(
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_semi_bold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold)
)