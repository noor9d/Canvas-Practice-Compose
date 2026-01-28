package com.imagination.canvaspractice.presentation.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

@Composable
fun ExitBottomSheet(
    modifier: Modifier = Modifier,
    onExitClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CanvasPracticeTheme.colorScheme.surface)
            .padding(CanvasPracticeTheme.spacing.medium)
    ) {
        Text(
            text = "Are you sure you want to exit?",
            style = CanvasPracticeTheme.typography.listingText,
            color = CanvasPracticeTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(CanvasPracticeTheme.shape.button)
                .background(color = Color.Transparent)
                .border(
                    shape = CanvasPracticeTheme.shape.button,
                    width = 1.dp,
                    color = CanvasPracticeTheme.colorScheme.outline
                )
                .clickable { onExitClicked() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tap to Exit",
                style = CanvasPracticeTheme.typography.listingText,
                color = CanvasPracticeTheme.colorScheme.onSurface
            )
        }
    }
}