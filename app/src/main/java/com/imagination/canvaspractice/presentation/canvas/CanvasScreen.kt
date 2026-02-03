package com.imagination.canvaspractice.presentation.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imagination.canvaspractice.domain.constants.DrawingConstants
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.SelectedItem
import com.imagination.canvaspractice.presentation.canvas.components.CanvasTopBar
import com.imagination.canvaspractice.presentation.canvas.components.DrawingCanvas
import com.imagination.canvaspractice.presentation.components.BottomNavigationBar
import com.imagination.canvaspractice.presentation.components.ColorPickerBar
import com.imagination.canvaspractice.presentation.components.PenOptionsBar
import com.imagination.canvaspractice.presentation.components.ShapeOptionsBar
import com.imagination.canvaspractice.presentation.components.TextOptionsBar
import com.imagination.canvaspractice.presentation.navigation.BackStack
import com.imagination.canvaspractice.presentation.sheets.BoardOption
import com.imagination.canvaspractice.presentation.sheets.BoardOptionsBottomSheet
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    boardId: Int,
    viewModel: CanvasViewModel,
    backStack: BackStack
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(boardId) {
        viewModel.registerUserEvent(UserEvent.LoadBoard(boardId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasPracticeTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = CanvasPracticeTheme.colorScheme.primary
                    )
                }
            }

            state.errorMessage != null -> {
                // Handle error state - could show error message UI here
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Add error UI component
                }
            }

            state.board != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DrawingCanvas(
                        paths = state.paths,
                        currentPath = state.currentPath,
                        textElements = state.textElements,
                        shapeElements = state.shapeElements,
                        currentShape = state.currentShape,
                        drawingMode = state.drawingMode,
                        selectedItem = state.selectedItem,
                        textInputPosition = state.textInputPosition,
                        textInput = state.currentTextInput,
                        selectedColor = state.selectedColor,
                        selectedFontSize = state.selectedFontSize,
                        initialScale = state.scale,
                        initialPanOffset = state.panOffset,
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

                    // Show mode-specific options bar (when drawing or when item selected) or main navigation bar
                    when {
                        state.drawingMode == DrawingMode.PEN -> {
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
                        state.drawingMode == DrawingMode.TEXT -> {
                            val defaultTextColor = CanvasPracticeTheme.colorScheme.onSurface
                            LaunchedEffect(state.drawingMode) {
                                if (state.selectedColor == DrawingConstants.DEFAULT_COLOR) {
                                    viewModel.onAction(
                                        DrawingAction.OnSelectColor(defaultTextColor)
                                    )
                                }
                            }
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
                        state.drawingMode == DrawingMode.SHAPE -> {
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
                        // Selection mode: show options bar for the selected item type
                        state.selectedItem is SelectedItem.PathItem -> {
                            PenOptionsBar(
                                selectedColor = state.selectedColor,
                                onColorClick = {
                                    viewModel.onAction(DrawingAction.OnShowColorPicker)
                                },
                                onClose = {
                                    viewModel.onAction(DrawingAction.OnDeselect)
                                }
                            )
                        }
                        state.selectedItem is SelectedItem.TextItem -> {
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
                                    viewModel.onAction(DrawingAction.OnDeselect)
                                }
                            )
                        }
                        state.selectedItem is SelectedItem.ShapeItem -> {
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
                                    viewModel.onAction(DrawingAction.OnDeselect)
                                }
                            )
                        }
                        else -> {
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

        CanvasTopBar(
            boardTitle = state.board?.title ?: "",
            onOptionsClick = {
                viewModel.registerUserEvent(
                    UserEvent.ShowSheet(CanvasSheet.BOARD_OPTION_SHEET)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(
                    top = CanvasPracticeTheme.spacing.medium,
                    start = CanvasPracticeTheme.spacing.medium,
                    end = CanvasPracticeTheme.spacing.medium
                )
        )
    }

    if (activeSheet != null) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.registerUserEvent(UserEvent.HideSheet)
            },
            sheetState = bottomSheetState,
            containerColor = CanvasPracticeTheme.colorScheme.surface
        ) {
            when (activeSheet) {
                CanvasSheet.BOARD_OPTION_SHEET -> {
                    val board = state.board
                    if (board != null) {
                        BoardOptionsBottomSheet(
                            onDismiss = {
                                viewModel.registerUserEvent(UserEvent.HideSheet)
                            },
                            onOptionSelected = { option ->
                                when (option) {
                                    is BoardOption.Delete -> {
                                        // TODO: Implement delete and navigate back
                                    }

                                    is BoardOption.Share -> {
                                        // TODO: Implement share functionality
                                    }

                                    is BoardOption.CopyLink -> {
                                        // TODO: Implement copy link functionality
                                    }

                                    is BoardOption.AddToFavourite -> {
                                        // TODO: Implement add to favourite functionality
                                    }
                                }
                            }
                        )
                    }
                }

                CanvasSheet.TOOLS_SHEET -> {
                    // TODO: Implement tools sheet
                }

                else -> {}
            }
        }
    }
}