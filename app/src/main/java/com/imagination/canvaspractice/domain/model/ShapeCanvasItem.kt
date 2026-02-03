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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import kotlin.math.min

/**
 * CanvasItem implementation for drawing shapes
 * Follows Single Responsibility Principle: Only handles shape drawing
 */
class ShapeCanvasItem(
    private var shapeData: ShapeData
) : CanvasItem {

    override fun getId(): String = shapeData.id

    override fun getItemType(): SelectedItem = SelectedItem.ShapeItem(shapeData.id)

    override fun draw(scope: DrawScope, textMeasurer: TextMeasurer) {
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
                scope.drawRect(
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
                scope.drawCircle(
                    color = shapeData.color,
                    radius = radius,
                    center = center,
                    style = style
                )
            }

            ShapeType.LINE -> {
                scope.drawLine(
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
                scope.drawPath(
                    path = path,
                    color = shapeData.color,
                    style = style
                )
            }
        }
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
        val bounds = getBounds()
        
        return when (shapeData.type) {
            ShapeType.RECTANGLE -> {
                bounds.contains(Offset(x, y))
            }
            
            ShapeType.CIRCLE -> {
                val center = Offset(
                    x = bounds.center.x,
                    y = bounds.center.y
                )
                val radius = min(bounds.width, bounds.height) / 2f
                val distance = kotlin.math.sqrt(
                    (x - center.x) * (x - center.x) + (y - center.y) * (y - center.y)
                )
                distance <= radius
            }
            
            ShapeType.LINE -> {
                val threshold = shapeData.strokeWidth / 2f + 24f
                val distance = distanceToLineSegment(
                    x, y,
                    shapeData.startPosition.x, shapeData.startPosition.y,
                    shapeData.endPosition.x, shapeData.endPosition.y
                )
                distance <= threshold
            }
            
            ShapeType.TRIANGLE -> {
                val topLeft = shapeData.topLeft
                val size = shapeData.size
                val p1 = Offset(topLeft.x + size.width / 2f, topLeft.y)
                val p2 = Offset(topLeft.x, topLeft.y + size.height)
                val p3 = Offset(topLeft.x + size.width, topLeft.y + size.height)
                isPointInTriangle(Offset(x, y), p1, p2, p3)
            }
        }
    }

    override fun translate(deltaX: Float, deltaY: Float) {
        shapeData = shapeData.copy(
            startPosition = Offset(
                shapeData.startPosition.x + deltaX,
                shapeData.startPosition.y + deltaY
            ),
            endPosition = Offset(
                shapeData.endPosition.x + deltaX,
                shapeData.endPosition.y + deltaY
            )
        )
    }

    override fun getBounds(): Rect {
        val topLeft = shapeData.topLeft
        val size = shapeData.size
        val padding = if (shapeData.type == ShapeType.LINE) {
            shapeData.strokeWidth / 2f
        } else {
            0f
        }
        
        return Rect(
            left = topLeft.x - padding,
            top = topLeft.y - padding,
            right = topLeft.x + size.width + padding,
            bottom = topLeft.y + size.height + padding
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
        val bounds = getBounds()
        val centerX = bounds.center.x
        val centerY = bounds.center.y

        shapeData = shapeData.copy(
            startPosition = Offset(
                centerX + (shapeData.startPosition.x - centerX) * zoom,
                centerY + (shapeData.startPosition.y - centerY) * zoom
            ),
            endPosition = Offset(
                centerX + (shapeData.endPosition.x - centerX) * zoom,
                centerY + (shapeData.endPosition.y - centerY) * zoom
            ),
            strokeWidth = shapeData.strokeWidth * zoom
        )
    }

    /**
     * Gets the underlying ShapeData
     */
    fun getShapeData(): ShapeData = shapeData

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

    /**
     * Checks if a point is inside a triangle using barycentric coordinates
     */
    private fun isPointInTriangle(
        point: Offset,
        v1: Offset,
        v2: Offset,
        v3: Offset
    ): Boolean {
        val d1 = sign(point, v1, v2)
        val d2 = sign(point, v2, v3)
        val d3 = sign(point, v3, v1)

        val hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0)
        val hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0)

        return !(hasNeg && hasPos)
    }

    private fun sign(p1: Offset, p2: Offset, p3: Offset): Float {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y)
    }
}
