package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.R

/**
 * Options bar for text mode
 * Shows font size controls, color indicator, and close button
 * 
 * @param modifier Modifier to be applied to the bar
 * @param selectedColor Currently selected color
 * @param fontSize Currently selected font size
 * @param onColorClick Callback when the color indicator is clicked
 * @param onFontSizeChange Callback when font size is changed
 * @param onClose Callback when the close button is clicked
 */
@Composable
fun TextOptionsBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    fontSize: Float,
    onColorClick: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Font size controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onFontSizeChange((fontSize - 4f).coerceAtLeast(12f)) }) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.circle_minus_svgrepo_com),
                    contentDescription = "Decrease font size",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
            Text(
                text = "${fontSize.toInt()}sp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { onFontSizeChange((fontSize + 4f).coerceAtMost(72f)) }) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.circle_plus_svgrepo_com),
                    contentDescription = "Increase font size",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
        
        // Color indicator
        ColorIndicator(
            selectedColor = selectedColor,
            onClick = onColorClick
        )
    }
}
