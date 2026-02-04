package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.imagination.canvaspractice.domain.constants.DrawingConstants
import com.imagination.canvaspractice.domain.model.SelectedItem
import com.imagination.canvaspractice.presentation.dashboard.model.Board

/**
 * Represents the complete state of the canvas screen, including board loading and drawing state
 * 
 * @param isLoading Whether the board is currently loading
 * @param errorMessage Error message if loading failed (null if no error)
 * @param board The loaded board information (null if not loaded or error)
 * @param drawingMode The currently active drawing mode (null when showing main navigation bar)
 * @param selectedColor The currently selected color for drawing
 * @param isColorPickerVisible Whether the color picker bar is currently visible
 * @param currentPath The path currently being drawn (null if not drawing)
 * @param paths List of completed paths on the canvas
 * @param textElements List of text elements on the canvas
 * @param shapeElements List of shape elements on the canvas
 * @param currentShape The shape currently being drawn (null if not drawing)
 * @param selectedShapeType The selected shape type (when in SHAPE mode)
 * @param selectedFontSize The selected font size (when in TEXT mode)
 */
data class DrawingState(
    // Board loading state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val board: Board? = null,
    
    // Drawing state
    val drawingMode: DrawingMode? = null,
    val selectedColor: Color = DrawingConstants.DEFAULT_COLOR,
    val isColorPickerVisible: Boolean = false,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val textElements: List<TextData> = emptyList(),
    val shapeElements: List<ShapeData> = emptyList(),
    val currentShape: ShapeData? = null,
    val selectedShapeType: ShapeType = ShapeType.RECTANGLE,
    val selectedFontSize: Float = DrawingConstants.DEFAULT_FONT_SIZE,
    val currentTextInput: String = "",
    val textInputPosition: Offset? = null,
    val textCreationScale: Float? = null, // Store scale when text input starts for proper font size calculation
    val editingTextId: String? = null, // When set, text overlay is editing this element (update on done)

    // Canvas size (reported by canvas for centering new text in ViewModel)
    val canvasSize: IntSize = IntSize.Zero,

    // Zoom/Pan state (persisted to database)
    val scale: Float = 1f,
    val panOffset: Offset = Offset.Zero,

    // Selection state (supports multiple selection e.g. via Lasso)
    val selectedItems: List<SelectedItem> = emptyList(),

    // Lasso tool: when true, next stroke on canvas selects paths inside the drawn shape
    val isLassoMode: Boolean = false,
    val currentLassoPath: List<Offset>? = null,

    // Grouping: itemId -> groupId (items with same groupId are grouped)
    val itemGroups: Map<String, String> = emptyMap()
) {
    /** Single selected item when exactly one is selected; null otherwise. */
    val selectedItem: SelectedItem? get() = selectedItems.singleOrNull()
}