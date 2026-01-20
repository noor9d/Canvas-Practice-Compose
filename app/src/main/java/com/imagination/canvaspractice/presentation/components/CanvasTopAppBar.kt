package com.imagination.canvaspractice.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasTopAppBar(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    title: String = "",
    contentColor: Color = Color.Transparent,
    titleColor: Color = if (isDarkMode) Color.White else Color.Black,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressClicked: () -> Unit = {},
    showBackNavIcon: Boolean = false,
    fontSize: TextUnit? = null,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = contentColor,
            navigationIconContentColor = titleColor,
            titleContentColor = titleColor,
            actionIconContentColor = titleColor
        ),
        navigationIcon = {
            if (showBackNavIcon) {
                IconButton(onClick = onBackPressClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        title = {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = fontSize ?: MaterialTheme.typography.titleLarge.fontSize
                    )
                )
            }
        },
        actions = actions
    )
}

@Preview(showBackground = true)
@Composable
private fun TopAppBarPreview() {
    CanvasPracticeTheme {
        CanvasTopAppBar(title = "Canvas Practice")
    }
}