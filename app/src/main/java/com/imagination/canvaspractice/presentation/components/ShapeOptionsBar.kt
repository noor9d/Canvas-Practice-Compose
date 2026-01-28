package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.imagination.canvaspractice.domain.model.ShapeType
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

/**
 * Options bar for shape mode
 * Shows shape type selector, color indicator, and close button
 * 
 * @param modifier Modifier to be applied to the bar
 * @param selectedColor Currently selected color
 * @param selectedShapeType Currently selected shape type
 * @param onColorClick Callback when the color indicator is clicked
 * @param onSelectShapeType Callback when a shape type is selected
 * @param onClose Callback when the close button is clicked
 */
@Composable
fun ShapeOptionsBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    selectedShapeType: ShapeType,
    onColorClick: () -> Unit,
    onSelectShapeType: (ShapeType) -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasPracticeTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = CanvasPracticeTheme.colorScheme.onBackground
            )
        }
        
        // Shape type selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShapeTypeButton(
                shapeType = ShapeType.RECTANGLE,
                iconRes = R.drawable.square_svgrepo_com,
                isSelected = selectedShapeType == ShapeType.RECTANGLE,
                onClick = { onSelectShapeType(ShapeType.RECTANGLE) }
            )
            ShapeTypeButton(
                shapeType = ShapeType.CIRCLE,
                iconRes = R.drawable.circle_svgrepo_com,
                isSelected = selectedShapeType == ShapeType.CIRCLE,
                onClick = { onSelectShapeType(ShapeType.CIRCLE) }
            )
            ShapeTypeButton(
                shapeType = ShapeType.LINE,
                iconRes = R.drawable.line_svgrepo_com,
                isSelected = selectedShapeType == ShapeType.LINE,
                onClick = { onSelectShapeType(ShapeType.LINE) }
            )
            ShapeTypeButton(
                shapeType = ShapeType.TRIANGLE,
                iconRes = R.drawable.triangle_svgrepo_com,
                isSelected = selectedShapeType == ShapeType.TRIANGLE,
                onClick = { onSelectShapeType(ShapeType.TRIANGLE) }
            )
        }
        
        // Color indicator
        ColorIndicator(
            selectedColor = selectedColor,
            onClick = onColorClick
        )
    }
}

@Composable
private fun ShapeTypeButton(
    shapeType: ShapeType,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = CanvasPracticeTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            contentDescription = shapeType.name,
            colorFilter = ColorFilter.tint(
                if (isSelected) {
                    CanvasPracticeTheme.colorScheme.onBackground
                } else {
                    CanvasPracticeTheme.colorScheme.onBackground
                }
            )
        )
    }
}
