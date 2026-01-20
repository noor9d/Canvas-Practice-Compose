package com.imagination.canvaspractice.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.ShapeType
import com.imagination.canvaspractice.domain.model.TextData

/**
 * A composable canvas for drawing with touch gestures
 * Supports multiple drawing modes: Pen, Text, and Shapes
 * 
 * @param modifier Modifier to be applied to the canvas
 * @param paths List of completed paths to render
 * @param currentPath The path currently being drawn (if any)
 * @param textElements List of text elements to render
 * @param shapeElements List of shape elements to render
 * @param currentShape The shape currently being drawn (if any)
 * @param drawingMode The current drawing mode (null means no active mode)
 * @param textInputPosition Position where text input should appear (null if not showing)
 * @param textInput Current text input value
 * @param onTextInputChange Callback when text input changes
 * @param onTextInputDone Callback when text input is done (IME action)
 * @param onAction Callback for drawing actions
 */
@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    paths: List<PathData>,
    currentPath: PathData?,
    textElements: List<TextData>,
    shapeElements: List<ShapeData>,
    currentShape: ShapeData?,
    drawingMode: DrawingMode?,
    textInputPosition: Offset?,
    textInput: String,
    onTextInputChange: (String) -> Unit,
    onTextInputDone: () -> Unit,
    onAction: (DrawingAction) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val focusRequester = remember { FocusRequester() }

    // Auto-focus when text input position is set
    LaunchedEffect(textInputPosition) {
        if (textInputPosition != null) {
            focusRequester.requestFocus()
        }
    }

    Box(modifier = modifier.clipToBounds()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(drawingMode) {
                    when (drawingMode) {
                        DrawingMode.PEN -> {
                            detectDragGestures(
                                onDragStart = {
                                    onAction(DrawingAction.OnNewPathStart)
                                },
                                onDragEnd = {
                                    onAction(DrawingAction.OnPathEnd)
                                },
                                onDrag = { change, _ ->
                                    onAction(DrawingAction.OnDraw(change.position))
                                },
                                onDragCancel = {
                                    onAction(DrawingAction.OnPathEnd)
                                }
                            )
                        }

                        DrawingMode.SHAPE -> {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    onAction(DrawingAction.OnShapeStart(offset))
                                },
                                onDragEnd = {
                                    onAction(DrawingAction.OnShapeEnd)
                                },
                                onDrag = { change, _ ->
                                    onAction(DrawingAction.OnShapeUpdate(change.position))
                                },
                                onDragCancel = {
                                    onAction(DrawingAction.OnShapeEnd)
                                }
                            )
                        }

                        DrawingMode.TEXT -> {
                            detectTapGestures { offset ->
                                onAction(DrawingAction.OnTextInputStart(offset))
                            }
                        }

                        null -> {
                            // No gestures when no mode is selected
                        }
                    }
                }
        ) {
            // Draw all completed paths
            paths.fastForEach { pathData ->
                drawPath(
                    path = pathData.path,
                    color = pathData.color,
                    strokeWidth = pathData.strokeWidth
                )
            }

            // Draw the current path being drawn
            currentPath?.let {
                drawPath(
                    path = it.path,
                    color = it.color,
                    strokeWidth = it.strokeWidth
                )
            }

            // Draw all completed shapes
            shapeElements.fastForEach { shapeData ->
                drawShape(shapeData)
            }

            // Draw the current shape being drawn
            currentShape?.let {
                drawShape(it)
            }

            // Draw all text elements
            textElements.fastForEach { textData ->
                val textLayoutResult = textMeasurer.measure(
                    text = textData.text,
                    style = TextStyle(
                        color = textData.color,
                        fontSize = textData.fontSize.sp
                    )
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = textData.position
                )
            }
        }

        // Show text input overlay when position is set
        textInputPosition?.let { position ->
            val offsetX = with(density) { position.x.toDp() }
            val offsetY = with(density) { position.y.toDp() }

            OutlinedTextField(
                value = textInput,
                onValueChange = onTextInputChange,
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .widthIn(max = 200.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Type something",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onTextInputDone()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        }
    }
}

/**
 * Draws a smoothed path from a list of offsets.
 * Uses quadratic bezier curves for smooth line rendering.
 */
private fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    strokeWidth: Float = 10f
) {
    if (path.isEmpty()) return
    
    val smoothedPath = Path().apply {
        moveTo(path.first().x, path.first().y)

        // Smooth the path using quadratic bezier curves
        // This creates a more natural drawing experience
        for (i in 1 until path.size) {
            val previousPoint = path[i - 1]
            val currentPoint = path[i]
            
            // Use the midpoint as control point for smoother curves
            val controlPoint = Offset(
                x = (previousPoint.x + currentPoint.x) / 2f,
                y = (previousPoint.y + currentPoint.y) / 2f
            )
            
            quadraticTo(
                x1 = controlPoint.x,
                y1 = controlPoint.y,
                x2 = currentPoint.x,
                y2 = currentPoint.y
            )
        }
    }

    drawPath(
        path = smoothedPath,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Draws a shape based on its type and properties
 */
private fun DrawScope.drawShape(shapeData: ShapeData) {
    val topLeft = shapeData.topLeft
    val size = shapeData.size
    
    val style = if (shapeData.isFilled) {
        androidx.compose.ui.graphics.drawscope.Fill
    } else {
        Stroke(
            width = shapeData.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    }
    
    when (shapeData.type) {
        ShapeType.RECTANGLE -> {
            drawRect(
                color = shapeData.color,
                topLeft = topLeft,
                size = size,
                style = style
            )
        }
        ShapeType.CIRCLE -> {
            val radius = kotlin.math.min(size.width, size.height) / 2f
            val center = Offset(
                x = topLeft.x + size.width / 2f,
                y = topLeft.y + size.height / 2f
            )
            drawCircle(
                color = shapeData.color,
                radius = radius,
                center = center,
                style = style
            )
        }
        ShapeType.LINE -> {
            drawLine(
                color = shapeData.color,
                start = shapeData.startPosition,
                end = shapeData.endPosition,
                strokeWidth = shapeData.strokeWidth,
                cap = StrokeCap.Round
            )
        }
        ShapeType.TRIANGLE -> {
            val path = Path().apply {
                moveTo(
                    x = topLeft.x + size.width / 2f,
                    y = topLeft.y
                )
                lineTo(
                    x = topLeft.x,
                    y = topLeft.y + size.height
                )
                lineTo(
                    x = topLeft.x + size.width,
                    y = topLeft.y + size.height
                )
                close()
            }
            drawPath(
                path = path,
                color = shapeData.color,
                style = style
            )
        }
    }
}