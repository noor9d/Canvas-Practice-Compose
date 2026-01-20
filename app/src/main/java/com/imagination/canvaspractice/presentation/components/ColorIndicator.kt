package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Color indicator component that shows the selected color
 * Clickable to open the color picker
 * 
 * @param modifier Modifier to be applied to the indicator
 * @param selectedColor Currently selected color
 * @param onClick Callback when the color indicator is clicked
 */
@Composable
fun ColorIndicator(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(selectedColor)
        )
    }
}
