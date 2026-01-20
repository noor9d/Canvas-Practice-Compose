package com.imagination.canvaspractice.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.imagination.canvaspractice.domain.constants.DrawingConstants
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.DrawingState
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.ShapeType
import com.imagination.canvaspractice.domain.model.TextData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DrawingState(
            drawingMode = null, // Start with navigation bar visible
            selectedColor = DrawingConstants.DEFAULT_COLOR
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClicked()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnSelectMode -> onSelectMode(action.mode)
            DrawingAction.OnCloseModeOptions -> onCloseModeOptions()
            DrawingAction.OnShowColorPicker -> onShowColorPicker()
            DrawingAction.OnHideColorPicker -> onHideColorPicker()
            is DrawingAction.OnSelectShapeType -> onSelectShapeType(action.shapeType)
            is DrawingAction.OnFontSizeChange -> onFontSizeChange(action.fontSize)
            is DrawingAction.OnTextInputChange -> onTextInputChange(action.text)
            is DrawingAction.OnTextInputStart -> onTextInputStart(action.position)
            DrawingAction.OnTextInputDone -> onTextInputDone()
            is DrawingAction.OnShapeStart -> onShapeStart(action.offset)
            is DrawingAction.OnShapeUpdate -> onShapeUpdate(action.offset)
            DrawingAction.OnShapeEnd -> onShapeEnd()
        }
    }

    private fun onSelectMode(mode: DrawingMode) {
        _state.update { 
            it.copy(
                drawingMode = mode,
                textInputPosition = null,
                currentTextInput = ""
            ) 
        }
    }

    private fun onCloseModeOptions() {
        _state.update { 
            it.copy(
                drawingMode = null,
                textInputPosition = null,
                currentTextInput = ""
            ) 
        }
    }

    private fun onSelectShapeType(shapeType: ShapeType) {
        _state.update { it.copy(selectedShapeType = shapeType) }
    }

    private fun onFontSizeChange(fontSize: Float) {
        _state.update { it.copy(selectedFontSize = fontSize) }
    }

    private fun onShapeStart(offset: Offset) {
        if (_state.value.drawingMode != DrawingMode.SHAPE) return
        _state.update {
            it.copy(
                currentShape = ShapeData(
                    id = System.currentTimeMillis().toString(),
                    type = it.selectedShapeType,
                    startPosition = offset,
                    endPosition = offset,
                    color = it.selectedColor,
                    strokeWidth = DrawingConstants.DEFAULT_STROKE_WIDTH
                )
            )
        }
    }

    private fun onShapeUpdate(offset: Offset) {
        val currentShape = _state.value.currentShape ?: return
        _state.update {
            it.copy(
                currentShape = currentShape.copy(endPosition = offset)
            )
        }
    }

    private fun onShapeEnd() {
        val currentShape = _state.value.currentShape ?: return
        _state.update {
            it.copy(
                currentShape = null,
                shapeElements = it.shapeElements + currentShape
            )
        }
    }

    private fun onTextInputChange(text: String) {
        _state.update { it.copy(currentTextInput = text) }
    }

    private fun onTextInputStart(position: Offset) {
        if (_state.value.drawingMode != DrawingMode.TEXT) return
        _state.update {
            it.copy(
                textInputPosition = position,
                currentTextInput = ""
            )
        }
    }

    private fun onTextInputDone() {
        val position = _state.value.textInputPosition ?: return
        val textToAdd = _state.value.currentTextInput.ifBlank { "Text" }
        _state.update {
            it.copy(
                textInputPosition = null,
                currentTextInput = "",
                textElements = it.textElements + TextData(
                    id = System.currentTimeMillis().toString(),
                    text = textToAdd,
                    position = position,
                    color = it.selectedColor,
                    fontSize = it.selectedFontSize
                )
            )
        }
    }

    private fun onSelectColor(color: Color) {
        _state.update { 
            it.copy(
                selectedColor = color,
                isColorPickerVisible = false // Close color picker when color is selected
            ) 
        }
    }

    private fun onShowColorPicker() {
        _state.update { it.copy(isColorPickerVisible = true) }
    }

    private fun onHideColorPicker() {
        _state.update { it.copy(isColorPickerVisible = false) }
    }

    private fun onPathEnd() {
        val currentPathData = _state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }

    private fun onNewPathStart() {
        if (_state.value.drawingMode != DrawingMode.PEN) return
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = it.selectedColor,
                    strokeWidth = DrawingConstants.DEFAULT_STROKE_WIDTH,
                    path = emptyList()
                )
            )
        }
    }

    private fun onDraw(offset: Offset) {
        if (_state.value.drawingMode != DrawingMode.PEN) return
        val currentPathData = _state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    private fun onClearCanvasClicked() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList(),
                textElements = emptyList(),
                shapeElements = emptyList(),
                currentShape = null
            )
        }
    }
}

sealed interface DrawingAction {
    // Pen drawing actions
    data object OnNewPathStart: DrawingAction
    data class OnDraw(val offset: Offset): DrawingAction
    data object OnPathEnd: DrawingAction
    
    // Mode selection actions
    data class OnSelectMode(val mode: DrawingMode): DrawingAction
    data object OnCloseModeOptions: DrawingAction
    
    // Color selection
    data class OnSelectColor(val color: Color): DrawingAction
    data object OnShowColorPicker: DrawingAction
    data object OnHideColorPicker: DrawingAction
    
    // Shape actions
    data class OnSelectShapeType(val shapeType: ShapeType): DrawingAction
    data class OnShapeStart(val offset: Offset): DrawingAction
    data class OnShapeUpdate(val offset: Offset): DrawingAction
    data object OnShapeEnd: DrawingAction
    
    // Text actions
    data class OnFontSizeChange(val fontSize: Float): DrawingAction
    data class OnTextInputChange(val text: String): DrawingAction
    data class OnTextInputStart(val position: Offset): DrawingAction
    data object OnTextInputDone: DrawingAction
    
    // Canvas actions
    data object OnClearCanvasClick: DrawingAction
}