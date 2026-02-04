package com.imagination.canvaspractice.presentation.canvas.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imagination.canvaspractice.domain.constants.DrawingConstants.GRID_COLOR
import com.imagination.canvaspractice.domain.constants.DrawingConstants.GRID_SIZE
import com.imagination.canvaspractice.domain.constants.DrawingConstants.MAX_SCALE
import com.imagination.canvaspractice.domain.constants.DrawingConstants.MIN_SCALE
import com.imagination.canvaspractice.domain.model.CanvasItem
import com.imagination.canvaspractice.domain.model.CanvasItemFactory
import com.imagination.canvaspractice.domain.model.DrawingMode
import com.imagination.canvaspractice.domain.model.PathCanvasItem
import com.imagination.canvaspractice.domain.model.PathData
import com.imagination.canvaspractice.domain.model.SelectedItem
import com.imagination.canvaspractice.domain.model.ShapeCanvasItem
import com.imagination.canvaspractice.domain.model.ShapeData
import com.imagination.canvaspractice.domain.model.TextCanvasItem
import com.imagination.canvaspractice.domain.model.TextData
import com.imagination.canvaspractice.presentation.canvas.DrawingAction
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

private enum class ResizeHandle {
    TopLeft, TopCenter, TopRight, MiddleRight, BottomRight, BottomCenter, BottomLeft, MiddleLeft
}

private fun ResizeHandle.position(bounds: Rect): Offset = when (this) {
    ResizeHandle.TopLeft -> Offset(bounds.left, bounds.top)
    ResizeHandle.TopCenter -> Offset(bounds.center.x, bounds.top)
    ResizeHandle.TopRight -> Offset(bounds.right, bounds.top)
    ResizeHandle.MiddleRight -> Offset(bounds.right, bounds.center.y)
    ResizeHandle.BottomRight -> Offset(bounds.right, bounds.bottom)
    ResizeHandle.BottomCenter -> Offset(bounds.center.x, bounds.bottom)
    ResizeHandle.BottomLeft -> Offset(bounds.left, bounds.bottom)
    ResizeHandle.MiddleLeft -> Offset(bounds.left, bounds.center.y)
}

/** Extra space between selection box and resize handles to avoid conflict with move gesture. */
private const val RESIZE_HANDLE_BOX_PADDING = 24f
/** Touch target for resize handles; must be larger than dot so resize always wins over move. */
private const val RESIZE_HANDLE_HIT_RADIUS = 40f
/** Visible radius of each resize handle dot. */
private const val RESIZE_HANDLE_DOT_RADIUS = 16f
/** Thickness of the primary-colored ring (border) around each dot. */
private const val RESIZE_HANDLE_DOT_BORDER_THICKNESS = 3f

private fun hitTestResizeHandle(bounds: Rect, point: Offset, scale: Float): ResizeHandle? {
    val hitRadiusCanvas = (RESIZE_HANDLE_HIT_RADIUS / scale.coerceAtLeast(0.1f)).coerceAtLeast(8f)
    val hitRadiusSq = hitRadiusCanvas * hitRadiusCanvas
    return ResizeHandle.entries.firstOrNull { handle ->
        val pos = handle.position(bounds)
        (point.x - pos.x) * (point.x - pos.x) + (point.y - pos.y) * (point.y - pos.y) <= hitRadiusSq
    }
}

private fun computeScaleFromHandle(
    handle: ResizeHandle,
    initialBounds: Rect,
    newHandlePosition: Offset
): Pair<Float, Float> {
    val l = initialBounds.left
    val t = initialBounds.top
    val r = initialBounds.right
    val b = initialBounds.bottom
    val w = (r - l).coerceAtLeast(1f)
    val h = (b - t).coerceAtLeast(1f)
    return when (handle) {
        ResizeHandle.BottomRight -> {
            val newW = (newHandlePosition.x - l).coerceAtLeast(4f)
            val newH = (newHandlePosition.y - t).coerceAtLeast(4f)
            Pair(newW / w, newH / h)
        }

        ResizeHandle.BottomLeft -> {
            val newW = (r - newHandlePosition.x).coerceAtLeast(4f)
            val newH = (newHandlePosition.y - t).coerceAtLeast(4f)
            Pair(newW / w, newH / h)
        }

        ResizeHandle.TopRight -> {
            val newW = (newHandlePosition.x - l).coerceAtLeast(4f)
            val newH = (b - newHandlePosition.y).coerceAtLeast(4f)
            Pair(newW / w, newH / h)
        }

        ResizeHandle.TopLeft -> {
            val newW = (r - newHandlePosition.x).coerceAtLeast(4f)
            val newH = (b - newHandlePosition.y).coerceAtLeast(4f)
            Pair(newW / w, newH / h)
        }

        ResizeHandle.TopCenter -> {
            val newH = (b - newHandlePosition.y).coerceAtLeast(4f)
            Pair(1f, newH / h)
        }

        ResizeHandle.BottomCenter -> {
            val newH = (newHandlePosition.y - t).coerceAtLeast(4f)
            Pair(1f, newH / h)
        }

        ResizeHandle.MiddleLeft -> {
            val newW = (r - newHandlePosition.x).coerceAtLeast(4f)
            Pair(newW / w, 1f)
        }

        ResizeHandle.MiddleRight -> {
            val newW = (newHandlePosition.x - l).coerceAtLeast(4f)
            Pair(newW / w, 1f)
        }
    }
}

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
 * @param editingTextId When set, this text element is being edited (overlay shown); do not draw it on canvas to avoid duplication
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
    selectedItems: List<SelectedItem> = emptyList(),
    isLassoMode: Boolean = false,
    currentLassoPath: List<Offset>? = null,
    textInputPosition: Offset?,
    textInput: String,
    editingTextId: String? = null,
    selectedColor: Color,
    selectedFontSize: Float,
    initialScale: Float = 1f,
    initialPanOffset: Offset = Offset.Zero,
    onTextInputChange: (String) -> Unit,
    onTextInputDone: () -> Unit,
    onAction: (DrawingAction) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val focusRequester = remember { FocusRequester() }

    // Zoom/Pan state (local for smooth performance, initialized from state)
    var scale by remember(initialScale) { mutableFloatStateOf(initialScale) }
    var offset by remember(initialPanOffset) { mutableStateOf(initialPanOffset) }
    var isZooming by remember { mutableStateOf(false) }

    // Sync with initial values when they change (e.g., when board loads)
    LaunchedEffect(initialScale, initialPanOffset) {
        // Only update if there's a significant difference to avoid conflicts during gestures
        val scaleDiff = kotlin.math.abs(scale - initialScale)
        val panDiff = sqrt(
            (offset.x - initialPanOffset.x).toDouble().pow(2.0) +
                    (offset.y - initialPanOffset.y).toDouble().pow(2.0)
        ).toFloat()

        if (scaleDiff > 0.001f || panDiff > 1f) {
            scale = initialScale
            offset = initialPanOffset
        }
    }

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
        val viewportBottomRight =
            screenToCanvas(Offset(drawScope.size.width, drawScope.size.height))
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

    // Ref to current canvas items so pointerInput can read fresh data without paths/shapes/texts in keys
    // (having them in keys cancels the gesture on every move, causing jerky drag)
    val canvasItemsRef = remember { mutableStateOf<List<CanvasItem>>(emptyList()) }
    LaunchedEffect(paths, shapeElements, textElements) {
        canvasItemsRef.value = CanvasItemFactory.createItems(paths, shapeElements, textElements)
    }

    Box(modifier = modifier.clipToBounds()) {
        // Drawing gestures must be OUTSIDE the transformed Box to receive screen coordinates
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    onAction(DrawingAction.OnCanvasSizeChange(it))
                }
                .pointerInput(drawingMode, scale, offset, selectedItems, isLassoMode) {
                    val canvasItems = canvasItemsRef.value
                    when {
                        // When anything is selected (single or lasso), tap outside deselects; tap on selection allows move or resize
                        selectedItems.isNotEmpty() -> {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val canvasOffset = (down.position - offset) / scale
                                val combinedBounds = selectedItems.mapNotNull { selected ->
                                    canvasItems.find { it.getItemType() == selected }
                                        ?.let { s ->
                                            (s as? TextCanvasItem)?.getBounds(textMeasurer)
                                                ?: s.getBounds()
                                        }
                                }.fold<Rect?, Rect?>(null) { acc, r ->
                                    r?.let {
                                        if (acc == null) r else Rect(
                                            min(acc.left, r.left), min(acc.top, r.top),
                                            max(acc.right, r.right), max(acc.bottom, r.bottom)
                                        )
                                    }
                                }
                                val box = combinedBounds?.inflate(RESIZE_HANDLE_BOX_PADDING)
                                val hitHandle = box?.let { hitTestResizeHandle(it, canvasOffset, scale) }
                                if (hitHandle != null) {
                                    onAction(DrawingAction.OnResizeStart)
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        if (event.changes.all { !it.pressed }) {
                                            onAction(DrawingAction.OnResizeEnd)
                                            break
                                        }
                                        val currentPosition = event.changes.first().position
                                        val currentCanvas = (currentPosition - offset) / scale
                                        val (scaleX, scaleY) = computeScaleFromHandle(
                                            handle = hitHandle,
                                            initialBounds = box,
                                            newHandlePosition = currentCanvas
                                        )
                                        onAction(DrawingAction.OnScaleSelectedItem(scaleX, scaleY))
                                    }
                                    return@awaitEachGesture
                                }
                                // Move only when drag starts inside the selection box and NOT in handle zone (dots)
                                val inHandleZone = box?.let { hitTestResizeHandle(it, canvasOffset, scale) != null } == true
                                val tapInsideSelectionBox = combinedBounds != null
                                    && combinedBounds.contains(canvasOffset)
                                    && !inHandleZone
                                val hitItem = canvasItems.lastOrNull {
                                    it.containsPoint(
                                        canvasOffset.x,
                                        canvasOffset.y,
                                        textMeasurer
                                    )
                                }
                                if (tapInsideSelectionBox) {
                                    // Drag started inside selection box: enter move loop only
                                } else if (hitItem != null) {
                                    onAction(DrawingAction.OnSelectItem(hitItem.getItemType()))
                                } else {
                                    onAction(DrawingAction.OnDeselect)
                                    return@awaitEachGesture
                                }
                                var lastPosition = down.position
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    if (event.changes.all { !it.pressed }) break
                                    val currentPosition = event.changes.first().position
                                    val deltaScreen = currentPosition - lastPosition
                                    lastPosition = currentPosition
                                    if (deltaScreen.x != 0f || deltaScreen.y != 0f) {
                                        onAction(
                                            DrawingAction.OnMoveSelectedItem(
                                                deltaScreen.x / scale,
                                                deltaScreen.y / scale
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        DrawingMode.PEN == drawingMode -> {
                            if (isLassoMode) {
                                detectDragGestures(
                                    onDragStart = { screenOffset ->
                                        val canvasOffset = (screenOffset - offset) / scale
                                        onAction(DrawingAction.OnLassoStart(canvasOffset))
                                    },
                                    onDragEnd = { onAction(DrawingAction.OnLassoEnd) },
                                    onDrag = { change, _ ->
                                        val canvasOffset = (change.position - offset) / scale
                                        onAction(DrawingAction.OnLassoAddPoint(canvasOffset))
                                    },
                                    onDragCancel = { onAction(DrawingAction.OnLassoEnd) }
                                )
                            } else {
                                detectDragGestures(
                                    onDragStart = { screenOffset ->
                                        val canvasOffset = (screenOffset - offset) / scale
                                        onAction(DrawingAction.OnNewPathStart(scale))
                                        onAction(DrawingAction.OnDraw(canvasOffset))
                                    },
                                    onDragEnd = { onAction(DrawingAction.OnPathEnd) },
                                    onDrag = { change, _ ->
                                        val canvasOffset = (change.position - offset) / scale
                                        onAction(DrawingAction.OnDraw(canvasOffset))
                                    },
                                    onDragCancel = { onAction(DrawingAction.OnPathEnd) }
                                )
                            }
                        }

                        DrawingMode.SHAPE == drawingMode -> {
                            detectDragGestures(
                                onDragStart = { screenOffset ->
                                    val canvasOffset = (screenOffset - offset) / scale
                                    onAction(DrawingAction.OnShapeStart(canvasOffset, scale))
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

                        DrawingMode.TEXT == drawingMode -> {
                            detectTapGestures { screenOffset ->
                                val canvasOffset = (screenOffset - offset) / scale
                                onAction(DrawingAction.OnTextInputStart(canvasOffset, scale))
                            }
                        }

                        else -> {
                            // drawingMode == null: tap to select, tap outside to deselect
                            detectTapGestures(
                                onTap = { screenOffset ->
                                    val canvasOffset = (screenOffset - offset) / scale
                                    val hitItem = canvasItems.lastOrNull {
                                        it.containsPoint(
                                            canvasOffset.x,
                                            canvasOffset.y,
                                            textMeasurer
                                        )
                                    }
                                    if (hitItem != null) {
                                        onAction(DrawingAction.OnSelectItem(hitItem.getItemType()))
                                    } else {
                                        onAction(DrawingAction.OnDeselect)
                                    }
                                }
                            )
                        }
                    }
                }
                .pointerInput(drawingMode, selectedItems) {
                    if (drawingMode == null && selectedItems.isEmpty()) {
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

                            // Save zoom/pan to database
                            onAction(DrawingAction.OnZoomPanChange(newScale, newOffset))
                        }
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
            // Create canvas items from domain models (outside Canvas lambda)
            val canvasItems = remember(paths, shapeElements, textElements) {
                CanvasItemFactory.createItems(paths, shapeElements, textElements)
            }
            val selectionColor = CanvasPracticeTheme.colorScheme.primary

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Draw grid
                drawGrid(this)

                // Calculate viewport for visibility checking (in canvas coordinates)
                val viewportTopLeft = screenToCanvas(Offset.Zero)
                val viewportBottomRight = screenToCanvas(Offset(size.width, size.height))
                val viewport = Rect(viewportTopLeft, viewportBottomRight)

                // Draw all items (skip the text currently being edited so it's not duplicated with the overlay)
                canvasItems.forEach { item ->
                    if (editingTextId != null && item.getItemType() is SelectedItem.TextItem && (item.getItemType() as SelectedItem.TextItem).id == editingTextId) return@forEach
                    if (item.isVisible(viewport)) {
                        item.draw(this, textMeasurer)
                    }
                }

                // Draw one combined selection box wrapping all selected/grouped items (exclude text being edited)
                var combinedSelectionBounds: Rect? = null
                selectedItems.forEach { selected ->
                    if (editingTextId != null && selected is SelectedItem.TextItem && selected.id == editingTextId) return@forEach
                    val item = canvasItems.find { it.getItemType() == selected }
                        ?.takeIf { it.isVisible(viewport) }
                    if (item != null) {
                        val bounds =
                            (item as? TextCanvasItem)?.getBounds(textMeasurer) ?: item.getBounds()
                        combinedSelectionBounds = if (combinedSelectionBounds == null) bounds
                        else Rect(
                            min(combinedSelectionBounds.left, bounds.left),
                            min(combinedSelectionBounds.top, bounds.top),
                            max(combinedSelectionBounds.right, bounds.right),
                            max(combinedSelectionBounds.bottom, bounds.bottom)
                        )
                    }
                }
                combinedSelectionBounds?.let { bounds ->
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
                    drawRect(
                        color = selectionColor,
                        topLeft = Offset(bounds.left, bounds.top),
                        size = Size(bounds.width, bounds.height),
                        style = Stroke(width = 2f, pathEffect = pathEffect)
                    )
                }

                // Draw resize handle dots outside the selection box (with padding to avoid move/resize conflict)
                // Use radius/scale so dots stay same screen size regardless of zoom
                combinedSelectionBounds?.let { bounds ->
                    val box = bounds.inflate(RESIZE_HANDLE_BOX_PADDING)
                    val dotRadius = (RESIZE_HANDLE_DOT_RADIUS / scale.coerceAtLeast(0.1f)).coerceAtLeast(2f)
                    val innerRadius = (RESIZE_HANDLE_DOT_RADIUS - RESIZE_HANDLE_DOT_BORDER_THICKNESS) / scale.coerceAtLeast(0.1f)
                    ResizeHandle.entries.forEach { handle ->
                        val pos = handle.position(box)
                        drawCircle(
                            color = selectionColor.copy(alpha = 0.5f),
                            radius = dotRadius,
                            center = pos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = innerRadius.coerceAtLeast(1f),
                            center = pos
                        )
                    }
                }

                // Draw lasso path while drawing
                currentLassoPath?.let { lassoPoints ->
                    if (lassoPoints.size >= 2) {
                        val lassoPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(lassoPoints.first().x, lassoPoints.first().y)
                            lassoPoints.drop(1).forEach { p -> lineTo(p.x, p.y) }
                        }
                        drawPath(
                            path = lassoPath,
                            color = selectionColor,
                            style = Stroke(width = 2f)
                        )
                    }
                }

                // Draw current path being drawn (if any)
                currentPath?.let { pathData ->
                    val pathItem = PathCanvasItem(pathData)
                    pathItem.draw(this, textMeasurer)
                }

                // Draw current shape being drawn (if any)
                currentShape?.let { shapeData ->
                    val shapeItem = ShapeCanvasItem(shapeData)
                    shapeItem.draw(this, textMeasurer)
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
            // Convert canvas position to screen position for overlay (y only; x uses full width for centering)
            val screenY = canvasPosition.y * scale + offset.y
            val offsetY = with(density) { screenY.toDp() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = offsetY),
                contentAlignment = Alignment.Center
            ) {
                if (textInput.isEmpty()) {
                    Text(
                        text = "Type something",
                        color = CanvasPracticeTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = selectedFontSize.sp
                    )
                }
                BasicTextField(
                    value = textInput,
                    onValueChange = onTextInputChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = selectedColor,
                        fontSize = selectedFontSize.sp,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(CanvasPracticeTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onTextInputDone() }
                    ),
                    decorationBox = { innerTextField -> innerTextField() }
                )
            }
        }
    }
}