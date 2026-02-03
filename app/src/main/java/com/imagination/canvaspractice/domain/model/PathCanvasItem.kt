package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import kotlin.math.max
import kotlin.math.min

/**
 * CanvasItem implementation for drawing paths
 * Follows Single Responsibility Principle: Only handles path drawing
 */
class PathCanvasItem(
    private var pathData: PathData
) : CanvasItem {

    override fun getId(): String = pathData.id

    override fun getItemType(): SelectedItem = SelectedItem.PathItem(pathData.id)

    override fun draw(scope: DrawScope, textMeasurer: TextMeasurer) {
        if (pathData.path.isEmpty()) return

        val smoothedPath = Path().apply {
            moveTo(pathData.path.first().x, pathData.path.first().y)

            // Smooth the path using quadratic bezier curves
            for (i in 1 until pathData.path.size) {
                val previousPoint = pathData.path[i - 1]
                val currentPoint = pathData.path[i]

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

        scope.drawPath(
            path = smoothedPath,
            color = pathData.color,
            style = Stroke(
                width = pathData.strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    override fun drawSelection(scope: DrawScope, selectionColor: Color, textMeasurer: TextMeasurer?) {
        val bounds = getBounds()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
        scope.drawRect(
            color = selectionColor,
            topLeft = Offset(bounds.left, bounds.top),
            size = Size(bounds.width, bounds.height),
            style = Stroke(width = 2f, pathEffect = pathEffect)
        )
    }

    override fun containsPoint(x: Float, y: Float, textMeasurer: TextMeasurer?): Boolean {
        if (pathData.path.isEmpty()) return false
        // Quick reject: point must be within path bounds + padding
        val bounds = getBounds()
        val hitPadding = 24f
        if (x < bounds.left - hitPadding || x > bounds.right + hitPadding ||
            y < bounds.top - hitPadding || y > bounds.bottom + hitPadding) return false
        // Check if point is near any segment of the path (generous threshold for touch)
        val threshold = pathData.strokeWidth / 2f + 24f
        for (i in 0 until pathData.path.size - 1) {
            val p1 = pathData.path[i]
            val p2 = pathData.path[i + 1]
            
            // Calculate distance from point to line segment
            val distance = distanceToLineSegment(x, y, p1.x, p1.y, p2.x, p2.y)
            if (distance <= threshold) {
                return true
            }
        }
        
        return false
    }

    override fun translate(deltaX: Float, deltaY: Float) {
        pathData = pathData.copy(
            path = pathData.path.map { offset ->
                Offset(offset.x + deltaX, offset.y + deltaY)
            }
        )
    }

    override fun getBounds(): Rect {
        if (pathData.path.isEmpty()) {
            return Rect(0f, 0f, 0f, 0f)
        }

        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE

        pathData.path.forEach { offset ->
            minX = min(minX, offset.x)
            minY = min(minY, offset.y)
            maxX = max(maxX, offset.x)
            maxY = max(maxY, offset.y)
        }

        // Add padding for stroke width
        val padding = pathData.strokeWidth / 2f
        return Rect(
            left = minX - padding,
            top = minY - padding,
            right = maxX + padding,
            bottom = maxY + padding
        )
    }

    override fun intersects(rect: Rect): Boolean {
        val bounds = getBounds()
        return bounds.overlaps(rect)
    }

    override fun isVisible(viewport: Rect): Boolean {
        return intersects(viewport)
    }

    override fun resize(zoom: Float) {
        if (pathData.path.isEmpty()) return

        // Calculate center point
        val bounds = getBounds()
        val centerX = bounds.center.x
        val centerY = bounds.center.y

        // Scale all points relative to center
        pathData = pathData.copy(
            path = pathData.path.map { offset ->
                Offset(
                    x = centerX + (offset.x - centerX) * zoom,
                    y = centerY + (offset.y - centerY) * zoom
                )
            },
            strokeWidth = pathData.strokeWidth * zoom
        )
    }

    /**
     * Gets the underlying PathData
     */
    fun getPathData(): PathData = pathData

    /**
     * Calculates the distance from a point to a line segment
     */
    private fun distanceToLineSegment(
        px: Float, py: Float,
        x1: Float, y1: Float,
        x2: Float, y2: Float
    ): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        val lengthSquared = dx * dx + dy * dy

        if (lengthSquared == 0f) {
            // Line segment is a point
            val dx2 = px - x1
            val dy2 = py - y1
            return kotlin.math.sqrt(dx2 * dx2 + dy2 * dy2)
        }

        val t = ((px - x1) * dx + (py - y1) * dy) / lengthSquared
        val tClamped = t.coerceIn(0f, 1f)

        val closestX = x1 + tClamped * dx
        val closestY = y1 + tClamped * dy

        val dx2 = px - closestX
        val dy2 = py - closestY
        return kotlin.math.sqrt(dx2 * dx2 + dy2 * dy2)
    }
}
