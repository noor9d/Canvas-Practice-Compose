package com.imagination.canvaspractice.presentation.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.imagination.canvaspractice.core.base.BaseViewModel
import com.imagination.canvaspractice.data.mapper.BoardMapper.toDomain
import com.imagination.canvaspractice.domain.constants.DrawingConstants
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.DrawingState
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.ShapeType
import com.imagination.canvaspractice.domain.model.TextData
import com.imagination.canvaspractice.domain.repository.BoardRepository
import com.imagination.canvaspractice.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : BaseViewModel<UserEvent, UiEvent>() {

    private var currentBoardId: Long? = null
    private var saveZoomPanJob: Job? = null

    private val _state = MutableStateFlow(
        DrawingState(
            isLoading = false,
            errorMessage = null,
            board = null,
            drawingMode = null, // Start with navigation bar visible
            selectedColor = DrawingConstants.DEFAULT_COLOR
        )
    )
    val state = _state.asStateFlow()

    private val _activeSheet = MutableStateFlow<CanvasSheet?>(null)
    val activeSheet = _activeSheet.asStateFlow()

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvasClicked()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            is DrawingAction.OnNewPathStart -> onNewPathStart(action.scale)
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnSelectMode -> onSelectMode(action.mode)
            DrawingAction.OnCloseModeOptions -> onCloseModeOptions()
            DrawingAction.OnShowColorPicker -> onShowColorPicker()
            DrawingAction.OnHideColorPicker -> onHideColorPicker()
            is DrawingAction.OnSelectShapeType -> onSelectShapeType(action.shapeType)
            is DrawingAction.OnFontSizeChange -> onFontSizeChange(action.fontSize)
            is DrawingAction.OnTextInputChange -> onTextInputChange(action.text)
            is DrawingAction.OnTextInputStart -> onTextInputStart(action.position, action.scale)
            DrawingAction.OnTextInputDone -> onTextInputDone()
            is DrawingAction.OnShapeStart -> onShapeStart(action.offset, action.scale)
            is DrawingAction.OnShapeUpdate -> onShapeUpdate(action.offset)
            DrawingAction.OnShapeEnd -> onShapeEnd()
            is DrawingAction.OnZoomPanChange -> onZoomPanChange(action.scale, action.panOffset)
        }
    }

    private fun onSelectMode(mode: DrawingMode) {
        _state.update {
            it.copy(
                drawingMode = mode,
                textInputPosition = null,
                currentTextInput = "",
                isColorPickerVisible = false // Hide color picker when switching modes
            )
        }
    }

    private fun onCloseModeOptions() {
        _state.update {
            it.copy(
                drawingMode = null,
                textInputPosition = null,
                currentTextInput = "",
                isColorPickerVisible = false // Hide color picker when closing tool options
            )
        }
    }

    private fun onSelectShapeType(shapeType: ShapeType) {
        _state.update { it.copy(selectedShapeType = shapeType) }
    }

    private fun onFontSizeChange(fontSize: Float) {
        _state.update { it.copy(selectedFontSize = fontSize) }
    }

    private fun onShapeStart(offset: Offset, scale: Float) {
        if (_state.value.drawingMode != DrawingMode.SHAPE) return
        // Adjust stroke width for zoom level to maintain visual size
        val adjustedStrokeWidth = DrawingConstants.DEFAULT_STROKE_WIDTH / scale
        _state.update {
            it.copy(
                currentShape = ShapeData(
                    id = System.currentTimeMillis().toString(),
                    type = it.selectedShapeType,
                    startPosition = offset,
                    endPosition = offset,
                    color = it.selectedColor,
                    strokeWidth = adjustedStrokeWidth
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
        val boardId = currentBoardId ?: return
        
        _state.update {
            it.copy(
                currentShape = null,
                shapeElements = it.shapeElements + currentShape
            )
        }
        
        // Save to database
        viewModelScope.launch {
            boardRepository.insertShape(currentShape, boardId)
        }
    }

    private fun onTextInputChange(text: String) {
        _state.update { it.copy(currentTextInput = text) }
    }

    private fun onTextInputStart(position: Offset, scale: Float) {
        if (_state.value.drawingMode != DrawingMode.TEXT) return
        _state.update {
            it.copy(
                textInputPosition = position,
                currentTextInput = ""
            )
        }
        // Store scale for text creation
        _state.update { it.copy(textCreationScale = scale) }
    }

    private fun onTextInputDone() {
        val position = _state.value.textInputPosition ?: return
        val textToAdd = _state.value.currentTextInput.ifBlank { "Text" }
        val boardId = currentBoardId ?: return
        val scale = _state.value.textCreationScale ?: 1f
        
        // Adjust font size for zoom level to maintain visual size
        val adjustedFontSize = _state.value.selectedFontSize / scale
        
        val newText = TextData(
            id = System.currentTimeMillis().toString(),
            text = textToAdd,
            position = position,
            color = _state.value.selectedColor,
            fontSize = adjustedFontSize
        )
        
        _state.update {
            it.copy(
                textInputPosition = null,
                currentTextInput = "",
                textElements = it.textElements + newText,
                textCreationScale = null // Reset after text creation
            )
        }
        
        // Save to database
        viewModelScope.launch {
            boardRepository.insertText(newText, boardId)
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
        val boardId = currentBoardId ?: return
        
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
        
        // Save to database
        viewModelScope.launch {
            boardRepository.insertPath(currentPathData, boardId)
        }
    }

    private fun onNewPathStart(scale: Float) {
        if (_state.value.drawingMode != DrawingMode.PEN) return
        // Adjust stroke width for zoom level to maintain visual size
        val adjustedStrokeWidth = DrawingConstants.DEFAULT_STROKE_WIDTH / scale
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = it.selectedColor,
                    strokeWidth = adjustedStrokeWidth,
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
        val boardId = currentBoardId ?: return
        
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList(),
                textElements = emptyList(),
                shapeElements = emptyList(),
                currentShape = null
            )
        }
        
        // Clear from database
        viewModelScope.launch {
            boardRepository.deletePathsByBoardId(boardId)
            boardRepository.deleteTextsByBoardId(boardId)
            boardRepository.deleteShapesByBoardId(boardId)
        }
    }

    fun loadBoard(boardId: Int) = launchWithExceptionHandler {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        try {
            val boardIdLong = boardId.toLong()
            currentBoardId = boardIdLong
            
            // Load board entity
            val boardEntity = boardRepository.getBoardById(boardIdLong)
            if (boardEntity == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Board not found"
                    )
                }
                return@launchWithExceptionHandler
            }
            
            val board = boardEntity.toDomain()
            
            // Load all drawing elements
            val (paths, texts, shapes) = boardRepository.loadBoardData(boardIdLong)
            
        _state.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                board = board,
                paths = paths,
                textElements = texts,
                shapeElements = shapes,
                textCreationScale = null, // Reset scale after text creation
                scale = board.scale,
                panOffset = board.panOffset
            )
        }

        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load board"
                )
            }
        }
    }

    private fun onZoomPanChange(scale: Float, panOffset: Offset) {
        val boardId = currentBoardId ?: return
        
        // Update state immediately
        _state.update {
            it.copy(
                scale = scale,
                panOffset = panOffset
            )
        }
        
        // Cancel previous save job
        saveZoomPanJob?.cancel()
        
        // Save to database (debounced to avoid too many writes)
        saveZoomPanJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Wait 500ms after last change
            val boardEntity = boardRepository.getBoardById(boardId)
            if (boardEntity != null) {
                val updatedEntity = boardEntity.copy(
                    scale = scale,
                    panOffsetX = panOffset.x,
                    panOffsetY = panOffset.y,
                    updatedAt = System.currentTimeMillis()
                )
                boardRepository.updateBoard(updatedEntity)
            }
        }
    }

    override fun onUserEvent(event: UserEvent) {
        when (event) {
            UserEvent.HideSheet -> _activeSheet.value = null
            is UserEvent.ShowSheet -> _activeSheet.value = event.sheet
            is UserEvent.LoadBoard -> loadBoard(event.boardInt)
        }
    }
}

sealed interface UiEvent {
    data class Navigate(val screen: Screen) : UiEvent
}

sealed interface UserEvent {
    data class ShowSheet(val sheet: CanvasSheet) : UserEvent
    data object HideSheet : UserEvent
    data class LoadBoard(val boardInt: Int) : UserEvent
}

enum class CanvasSheet {
    BOARD_OPTION_SHEET,
    TOOLS_SHEET
}

sealed interface DrawingAction {
    // Pen drawing actions
    data class OnNewPathStart(val scale: Float) : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction

    // Mode selection actions
    data class OnSelectMode(val mode: DrawingMode) : DrawingAction
    data object OnCloseModeOptions : DrawingAction

    // Color selection
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnShowColorPicker : DrawingAction
    data object OnHideColorPicker : DrawingAction

    // Shape actions
    data class OnSelectShapeType(val shapeType: ShapeType) : DrawingAction
    data class OnShapeStart(val offset: Offset, val scale: Float) : DrawingAction
    data class OnShapeUpdate(val offset: Offset) : DrawingAction
    data object OnShapeEnd : DrawingAction

    // Text actions
    data class OnFontSizeChange(val fontSize: Float) : DrawingAction
    data class OnTextInputChange(val text: String) : DrawingAction
    data class OnTextInputStart(val position: Offset, val scale: Float) : DrawingAction
    data object OnTextInputDone : DrawingAction

    // Canvas actions
    data object OnClearCanvasClick : DrawingAction
    
    // Zoom/Pan actions
    data class OnZoomPanChange(val scale: Float, val panOffset: Offset) : DrawingAction
}