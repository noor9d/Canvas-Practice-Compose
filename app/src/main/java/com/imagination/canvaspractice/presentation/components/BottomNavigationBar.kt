package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.R
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

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
            .background(CanvasPracticeTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DrawingModeButton(
            iconRes = R.drawable.pen_svgrepo_com,
            label = "Pen",
            onClick = { onModeSelected(DrawingMode.PEN) }
        )
        DrawingModeButton(
            iconRes = R.drawable.text_selection_svgrepo_com,
            label = "Text",
            onClick = { onModeSelected(DrawingMode.TEXT) }
        )
        DrawingModeButton(
            iconRes = R.drawable.shapes_svgrepo_com,
            label = "Shape",
            onClick = { onModeSelected(DrawingMode.SHAPE) }
        )
    }
}

@Composable
private fun DrawingModeButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            contentDescription = label,
            colorFilter = ColorFilter.tint(CanvasPracticeTheme.colorScheme.onBackground)
        )
        Text(
            text = label,
            style = CanvasPracticeTheme.typography.text,
            color = CanvasPracticeTheme.colorScheme.onBackground
        )
    }
}
