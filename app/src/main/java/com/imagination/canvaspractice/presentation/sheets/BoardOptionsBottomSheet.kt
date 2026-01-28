package com.imagination.canvaspractice.presentation.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.imagination.canvaspractice.R
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

/**
 * Bottom sheet options for a board.
 */
sealed class BoardOption {
    data object Share : BoardOption()
    data object CopyLink : BoardOption()
    data object AddToFavourite : BoardOption()
    data object Delete : BoardOption()
}

@Composable
fun BoardOptionsBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelected: (BoardOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasPracticeTheme.colorScheme.surface)
            .padding(CanvasPracticeTheme.spacing.medium)
    ) {
        // Share option
        BoardOptionItem(
            iconRes = R.drawable.share_svgrepo_com,
            text = "Share",
            onClick = {
                onOptionSelected(BoardOption.Share)
                onDismiss()
            }
        )

        // Copy link option
        BoardOptionItem(
            iconRes = R.drawable.copy_link_svgrepo_com,
            text = "Copy link",
            onClick = {
                onOptionSelected(BoardOption.CopyLink)
                onDismiss()
            }
        )

        // Add to favourite option
        BoardOptionItem(
            iconRes = R.drawable.favourite_svgrepo_com,
            text = "Add to favourite",
            onClick = {
                onOptionSelected(BoardOption.AddToFavourite)
                onDismiss()
            }
        )

        // Delete option
        BoardOptionItem(
            iconRes = R.drawable.delete_2_svgrepo_com,
            text = "Delete",
            onClick = {
                onOptionSelected(BoardOption.Delete)
                onDismiss()
            },
            textColor = CanvasPracticeTheme.colorScheme.error
        )
    }
}

@Composable
private fun BoardOptionItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    textColor: Color = CanvasPracticeTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = CanvasPracticeTheme.spacing.medium,
                vertical = CanvasPracticeTheme.spacing.normal
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = text,
            colorFilter = ColorFilter.tint(textColor),
            modifier = Modifier.padding(end = CanvasPracticeTheme.spacing.medium)
        )
        Text(
            text = text,
            style = CanvasPracticeTheme.typography.listingText,
            color = textColor
        )
    }
}