package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
 * Options bar for pen drawing mode
 * Shows lasso tool, color indicator, close and optional delete
 *
 * @param onLassoClick Callback when lasso tool is clicked (toggle multi-selection mode)
 * @param isLassoMode True when lasso mode is active
 */
@Composable
fun PenOptionsBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    onColorClick: () -> Unit,
    onClose: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onLassoClick: () -> Unit = {},
    isLassoMode: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasPracticeTheme.colorScheme.background)
            .padding(vertical = 8.dp, horizontal = 16.dp),
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

            IconButton(
                onClick = onLassoClick,
                modifier = if (isLassoMode) Modifier.background(
                    color = CanvasPracticeTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                ) else Modifier
            ) {
                Image(
                    painter = painterResource(R.drawable.lasso_svgrepo_com),
                    contentDescription = "Lasso selection",
                    colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
                )
            }
        }
        ColorIndicator(
            selectedColor = selectedColor,
            onClick = onColorClick
        )
    }
}
