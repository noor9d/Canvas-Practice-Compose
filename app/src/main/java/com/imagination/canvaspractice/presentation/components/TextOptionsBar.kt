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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.R
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

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
 * @param onDelete Optional callback when delete is clicked (e.g. when an item is selected); if null, delete icon is hidden
 */
@Composable
fun TextOptionsBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    fontSize: Float,
    onColorClick: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onClose: () -> Unit,
    onDelete: (() -> Unit)? = null,
    showGroupButtons: Boolean = false,
    isGrouped: Boolean = false,
    onGroupClick: () -> Unit = {},
    onUngroupClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasPracticeTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = CanvasPracticeTheme.colorScheme.onBackground
                )
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Image(
                        painter = painterResource(R.drawable.delete_2_svgrepo_com),
                        contentDescription = "Delete",
                        colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
                    )
                }
            }
            if (showGroupButtons) {
                if (isGrouped) {
                    IconButton(onClick = onUngroupClick) {
                        Image(
                            painter = painterResource(R.drawable.ungroup_items_svgrepo_com),
                            contentDescription = "Ungroup items",
                            colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
                        )
                    }
                } else {
                    IconButton(onClick = onGroupClick) {
                        Image(
                            painter = painterResource(R.drawable.group_items_svgrepo_com),
                            contentDescription = "Group items",
                            colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
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
                    colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
                )
            }
            Text(
                text = "${fontSize.toInt()}sp",
                style = CanvasPracticeTheme.typography.labelNav,
                color = CanvasPracticeTheme.colorScheme.onBackground
            )
            IconButton(onClick = { onFontSizeChange((fontSize + 4f).coerceAtMost(72f)) }) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.circle_plus_svgrepo_com),
                    contentDescription = "Increase font size",
                    colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
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
