package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.graphics.Color
import com.imagination.canvaspractice.domain.constants.DrawingConstants

/**
 * Represents the current state of the drawing canvas
 * 
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
    val drawingMode: DrawingMode? = null,
    val selectedColor: Color = DrawingConstants.DEFAULT_COLOR,
    val isColorPickerVisible: Boolean = false,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList(),
    val textElements: List<TextData> = emptyList(),
    val shapeElements: List<ShapeData> = emptyList(),
    val currentShape: ShapeData? = null,
    val selectedShapeType: ShapeType = ShapeType.RECTANGLE,
    val selectedFontSize: Float = DrawingConstants.DEFAULT_FONT_SIZE
)