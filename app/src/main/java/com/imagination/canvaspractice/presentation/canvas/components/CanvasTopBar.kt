package com.imagination.canvaspractice.presentation.canvas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

/**
 * Top bar for the Canvas screen.
 * Shows board title on the left and options icon on the right.
 * 
 * @param boardTitle The title of the board to display
 * @param onOptionsClick Callback when the options icon is clicked
 * @param modifier Modifier for the top bar
 */
@Composable
fun CanvasTopBar(
    modifier: Modifier = Modifier,
    boardTitle: String,
    onOptionsClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CanvasPracticeTheme.shape.rounded)
            .background(CanvasPracticeTheme.colorScheme.background)
            .padding(
                start = CanvasPracticeTheme.spacing.medium,
                end = CanvasPracticeTheme.spacing.extraSmall,
                top = CanvasPracticeTheme.spacing.extraSmall,
                bottom = CanvasPracticeTheme.spacing.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Board title on the left
        Text(
            text = boardTitle,
            style = CanvasPracticeTheme.typography.dialogHeading,
            color = CanvasPracticeTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Options icon on the right
        IconButton(onClick = onOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Board Options",
                tint = CanvasPracticeTheme.colorScheme.onBackground
            )
        }
    }
}
