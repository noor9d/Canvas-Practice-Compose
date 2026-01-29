package com.imagination.canvaspractice.presentation.canvas.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.imagination.canvaspractice.domain.constants.DrawingConstants.GRID_COLOR
import com.imagination.canvaspractice.domain.constants.DrawingConstants.GRID_SIZE
import com.imagination.canvaspractice.domain.constants.DrawingConstants.MAX_SCALE
import com.imagination.canvaspractice.domain.constants.DrawingConstants.MIN_SCALE
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.ShapeType
import com.imagination.canvaspractice.domain.model.TextData
import com.imagination.canvaspractice.presentation.canvas.DrawingAction
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

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
    selectedColor: Color,
    selectedFontSize: Float,
    onTextInputChange: (String) -> Unit,
    onTextInputDone: () -> Unit,
    onAction: (DrawingAction) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val focusRequester = remember { FocusRequester() }

    // Zoom/Pan state (local for smooth performance)
    var scale by remember { mutableFloatStateOf(0.5f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isZooming by remember { mutableStateOf(false) }

    // Auto-focus when text input position is set
    LaunchedEffect(textInputPosition) {
        if (textInputPosition != null) {
            focusRequester.requestFocus()
        }
    }

    // Auto-hide zoom percentage after 1 second of no zoom changes
    LaunchedEffect(scale, isZooming) {
        if (isZooming) {
            delay(1000)
            isZooming = false
        }
    }

    fun screenToCanvas(screenOffset: Offset): Offset {
        return (screenOffset - offset) / scale
    }

    fun zoomingItemOrCanvas(
        pan: Offset,
        scale: Float,
        zoom: Float,
        minScale: Float,
        maxScale: Float,
        centroid: Offset,
        offset: Offset
    ): Pair<Offset, Float> {
        var scale1 = scale
        var offset1 = offset
        val previousScale = scale1
        val newScale = (scale1 * zoom).coerceIn(minScale, maxScale)
        val scaleChange = if (previousScale == 0f) 1f else newScale / previousScale
        val centroidToContent = centroid - offset1
        scale1 = newScale
        offset1 = centroid - centroidToContent * scaleChange
        offset1 += pan
        return Pair(offset1, scale1)
    }

    fun drawGrid(drawScope: DrawScope) {
        val viewportTopLeft = screenToCanvas(Offset.Zero)
        val viewportBottomRight = screenToCanvas(Offset(drawScope.size.width, drawScope.size.height))
        val viewport = Rect(viewportTopLeft, viewportBottomRight)
        
        val startX = floor(viewport.left / GRID_SIZE) * GRID_SIZE
        val endX = ceil(viewport.right / GRID_SIZE) * GRID_SIZE
        val startY = floor(viewport.top / GRID_SIZE) * GRID_SIZE
        val endY = ceil(viewport.bottom / GRID_SIZE) * GRID_SIZE
        
        // Draw vertical lines
        var x = startX
        while (x <= endX) {
            drawScope.drawLine(
                color = GRID_COLOR,
                start = Offset(x, viewport.top),
                end = Offset(x, viewport.bottom),
                strokeWidth = 1f / scale
            )
            x += GRID_SIZE
        }

        // Draw horizontal lines
        var y = startY
        while (y <= endY) {
            drawScope.drawLine(
                color = GRID_COLOR,
                start = Offset(viewport.left, y),
                end = Offset(viewport.right, y),
                strokeWidth = 1f / scale
            )
            y += GRID_SIZE
        }
    }

    Box(modifier = modifier.clipToBounds()) {
        // Drawing gestures must be OUTSIDE the transformed Box to receive screen coordinates
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(drawingMode, scale, offset) {
                    // Handle drawing gestures (receive screen coordinates, convert to canvas)
                    when (drawingMode) {
                        DrawingMode.PEN -> {
                            detectDragGestures(
                                onDragStart = { screenOffset ->
                                    val canvasOffset = (screenOffset - offset) / scale
                                    onAction(DrawingAction.OnNewPathStart)
                                    onAction(DrawingAction.OnDraw(canvasOffset))
                                },
                                onDragEnd = {
                                    onAction(DrawingAction.OnPathEnd)
                                },
                                onDrag = { change, _ ->
                                    val canvasOffset = (change.position - offset) / scale
                                    onAction(DrawingAction.OnDraw(canvasOffset))
                                },
                                onDragCancel = {
                                    onAction(DrawingAction.OnPathEnd)
                                }
                            )
                        }

                        DrawingMode.SHAPE -> {
                            detectDragGestures(
                                onDragStart = { screenOffset ->
                                    val canvasOffset = (screenOffset - offset) / scale
                                    onAction(DrawingAction.OnShapeStart(canvasOffset))
                                },
                                onDragEnd = {
                                    onAction(DrawingAction.OnShapeEnd)
                                },
                                onDrag = { change, _ ->
                                    val canvasOffset = (change.position - offset) / scale
                                    onAction(DrawingAction.OnShapeUpdate(canvasOffset))
                                },
                                onDragCancel = {
                                    onAction(DrawingAction.OnShapeEnd)
                                }
                            )
                        }

                        DrawingMode.TEXT -> {
                            detectTapGestures { screenOffset ->
                                val canvasOffset = (screenOffset - offset) / scale
                                onAction(DrawingAction.OnTextInputStart(canvasOffset))
                            }
                        }

                        null -> {
                            // No gestures when no mode is selected
                        }
                    }
                }
                .pointerInput(Unit) {
                    // Handle zoom/pan gestures (separate from drawing gestures)
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val (newOffset, newScale) = zoomingItemOrCanvas(
                            pan = pan,
                            scale = scale,
                            zoom = zoom,
                            minScale = MIN_SCALE,
                            maxScale = MAX_SCALE,
                            centroid = centroid,
                            offset = offset
                        )
                        
                        // Only show percentage when actually zooming (zoom factor != 1.0)
                        // This distinguishes zoom from pan gestures
                        if (kotlin.math.abs(zoom - 1.0f) > 0.01f) {
                            isZooming = true
                        }
                        
                        scale = newScale
                        offset = newOffset
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawGrid(this)

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
        }

        // Zoom percentage indicator (shown while zooming)
        if (isZooming) {
            // Map scale from [MIN_SCALE, MAX_SCALE] to [1%, 400%]
            val percentage = ((scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE)) * 399f + 1f
            val clampedPercentage = percentage.coerceIn(1f, 400f)
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "${clampedPercentage.toInt()}%",
                    style = CanvasPracticeTheme.typography.text,
                    color = CanvasPracticeTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Text input overlay OUTSIDE transformed Box so it stays at screen position
        textInputPosition?.let { canvasPosition ->
            // Convert canvas position to screen position for overlay
            val screenPosition = Offset(
                x = canvasPosition.x * scale + offset.x,
                y = canvasPosition.y * scale + offset.y
            )
            val offsetX = with(density) { screenPosition.x.toDp() }
            val offsetY = with(density) { screenPosition.y.toDp() }

            OutlinedTextField(
                value = textInput,
                onValueChange = onTextInputChange,
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .widthIn(max = 200.dp)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = selectedColor,
                    fontSize = selectedFontSize.sp
                ),
                placeholder = {
                    Text(
                        text = "Type something",
                        color = CanvasPracticeTheme.colorScheme.surface.copy(alpha = 0.5f)
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
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedTextColor = selectedColor,
                    unfocusedTextColor = selectedColor
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
        Fill
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
            val radius = min(size.width, size.height) / 2f
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