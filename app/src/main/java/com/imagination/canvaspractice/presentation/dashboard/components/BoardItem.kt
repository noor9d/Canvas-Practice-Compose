package com.imagination.canvaspractice.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import com.imagination.canvaspractice.presentation.dashboard.model.Board

/**
 * Composable for displaying a Board item in the grid.
 * 
 * @param board The board to display
 * @param onClick Callback when the item is clicked
 * @param onOptionsClick Callback when the three-dot icon is clicked
 */
@Composable
fun BoardItem(
    modifier: Modifier = Modifier,
    board: Board,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CanvasPracticeTheme.shape.smallTile)
            .border(
                width = 1.dp,
                color = CanvasPracticeTheme.colorScheme.outline,
                shape = CanvasPracticeTheme.shape.smallTile
            )
            .background(CanvasPracticeTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Thumbnail area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CanvasPracticeTheme.colorScheme.surfaceVariant)
            ) {
                // Thumbnail will be implemented when Room database is added
                // For now, showing a placeholder background
            }
            
            // Title at bottom with three-dot icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(CanvasPracticeTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = board.title,
                    style = CanvasPracticeTheme.typography.moduleHeading,
                    color = CanvasPracticeTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                IconButton(
                    onClick = {
                        onOptionsClick()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Board Options",
                        tint = CanvasPracticeTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable for the "Add New Board" item.
 * Shows a + icon at center and "New board" title at bottom.
 * 
 * @param onClick Callback when the item is clicked
 */
@Composable
fun AddNewBoardItem(
    modifier: Modifier = Modifier,
    title: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CanvasPracticeTheme.shape.smallTile)
            .border(
                width = 1.dp,
                color = CanvasPracticeTheme.colorScheme.outline,
                shape = CanvasPracticeTheme.shape.smallTile
            )
            .background(CanvasPracticeTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon area - centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Board",
                    tint = CanvasPracticeTheme.colorScheme.primary,
                    modifier = Modifier.padding(CanvasPracticeTheme.spacing.medium)
                )
            }
            
            // Title at bottom
            Text(
                text = title,
                style = CanvasPracticeTheme.typography.moduleHeading,
                color = CanvasPracticeTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(CanvasPracticeTheme.spacing.small),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
