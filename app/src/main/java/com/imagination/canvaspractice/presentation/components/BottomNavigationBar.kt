package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.domain.model.DrawingMode

/**
 * Bottom navigation bar showing all available drawing mode options
 * 
 * @param modifier Modifier to be applied to the bar
 * @param onModeSelected Callback when a drawing mode is selected
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onModeSelected: (DrawingMode) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DrawingModeButton(
            icon = Icons.Default.Create,
            label = "Pen",
            onClick = { onModeSelected(DrawingMode.PEN) }
        )
        DrawingModeButton(
            icon = Icons.Default.Edit,
            label = "Text",
            onClick = { onModeSelected(DrawingMode.TEXT) }
        )
        DrawingModeButton(
            icon = Icons.Default.AddCircle,
            label = "Shape",
            onClick = { onModeSelected(DrawingMode.SHAPE) }
        )
    }
}

@Composable
private fun DrawingModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
