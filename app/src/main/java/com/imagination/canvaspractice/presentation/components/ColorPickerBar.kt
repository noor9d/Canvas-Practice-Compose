package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.imagination.canvaspractice.domain.constants.DrawingConstants

/**
 * Reusable color picker bar component
 * Displays all available colors in a horizontal scrollable row
 * 
 * @param modifier Modifier to be applied to the bar
 * @param selectedColor Currently selected color
 * @param colors List of available colors to choose from
 * @param onSelectColor Callback when a color is selected
 */
@Composable
fun ColorPickerBar(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    colors: List<Color> = DrawingConstants.AVAILABLE_COLORS,
    onSelectColor: (Color) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        colors.fastForEach { color ->
            val isSelected = selectedColor == color
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = if (isSelected) 1.2f else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) {
                            Color.Black
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .clickable {
                        onSelectColor(color)
                    }
            )
        }
    }
}
