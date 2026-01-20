package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Options bar for pen drawing mode
 * Shows color indicator and close button
 * 
 * @param modifier Modifier to be applied to the bar
 * @param selectedColor Currently selected color
 * @param onColorClick Callback when the color indicator is clicked
 * @param onClose Callback when the close button is clicked
 */
@Composable
fun PenOptionsBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    onColorClick: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        ColorIndicator(
            selectedColor = selectedColor,
            onClick = onColorClick
        )
    }
}
