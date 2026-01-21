package com.imagination.canvaspractice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.presentation.components.BottomNavigationBar
import com.imagination.canvaspractice.presentation.components.CanvasTopAppBar
import com.imagination.canvaspractice.presentation.components.ColorPickerBar
import com.imagination.canvaspractice.presentation.components.PenOptionsBar
import com.imagination.canvaspractice.presentation.components.ShapeOptionsBar
import com.imagination.canvaspractice.presentation.components.TextOptionsBar
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasPracticeTheme {
                val viewModel = viewModel<MainViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CanvasTopAppBar(
                            title = "Canvas Practice",
                            actions = {
                                IconButton(onClick = {
                                    viewModel.onAction(DrawingAction.OnClearCanvasClick)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawingCanvas(
                            paths = state.paths,
                            currentPath = state.currentPath,
                            textElements = state.textElements,
                            shapeElements = state.shapeElements,
                            currentShape = state.currentShape,
                            drawingMode = state.drawingMode,
                            textInputPosition = state.textInputPosition,
                            textInput = state.currentTextInput,
                            onTextInputChange = {
                                viewModel.onAction(DrawingAction.OnTextInputChange(it))
                            },
                            onTextInputDone = {
                                viewModel.onAction(DrawingAction.OnTextInputDone)
                            },
                            onAction = viewModel::onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        
                        // Show color picker bar if visible (above option bars)
                        if (state.isColorPickerVisible) {
                            ColorPickerBar(
                                selectedColor = state.selectedColor,
                                onSelectColor = {
                                    viewModel.onAction(DrawingAction.OnSelectColor(it))
                                }
                            )
                        }
                        
                        // Show mode-specific options bar or main navigation bar
                        when (state.drawingMode) {
                            DrawingMode.PEN -> {
                                PenOptionsBar(
                                    selectedColor = state.selectedColor,
                                    onColorClick = {
                                        viewModel.onAction(DrawingAction.OnShowColorPicker)
                                    },
                                    onClose = {
                                        viewModel.onAction(DrawingAction.OnCloseModeOptions)
                                    }
                                )
                            }
                            DrawingMode.TEXT -> {
                                TextOptionsBar(
                                    selectedColor = state.selectedColor,
                                    fontSize = state.selectedFontSize,
                                    onColorClick = {
                                        viewModel.onAction(DrawingAction.OnShowColorPicker)
                                    },
                                    onFontSizeChange = {
                                        viewModel.onAction(DrawingAction.OnFontSizeChange(it))
                                    },
                                    onClose = {
                                        viewModel.onAction(DrawingAction.OnCloseModeOptions)
                                    }
                                )
                            }
                            DrawingMode.SHAPE -> {
                                ShapeOptionsBar(
                                    selectedColor = state.selectedColor,
                                    selectedShapeType = state.selectedShapeType,
                                    onColorClick = {
                                        viewModel.onAction(DrawingAction.OnShowColorPicker)
                                    },
                                    onSelectShapeType = {
                                        viewModel.onAction(DrawingAction.OnSelectShapeType(it))
                                    },
                                    onClose = {
                                        viewModel.onAction(DrawingAction.OnCloseModeOptions)
                                    }
                                )
                            }
                            null -> {
                                BottomNavigationBar(
                                    onModeSelected = { mode ->
                                        viewModel.onAction(DrawingAction.OnSelectMode(mode))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanvasPracticeTheme {
    }
}