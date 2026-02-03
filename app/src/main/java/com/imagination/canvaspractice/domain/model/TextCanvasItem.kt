package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.sp

/**
 * CanvasItem implementation for drawing text
 * Follows Single Responsibility Principle: Only handles text drawing
 */
class TextCanvasItem(
    private var textData: TextData
) : CanvasItem {

    override fun getId(): String = textData.id

    override fun getItemType(): SelectedItem = SelectedItem.TextItem(textData.id)

    override fun draw(scope: DrawScope, textMeasurer: TextMeasurer) {
        val textLayoutResult = textMeasurer.measure(
            text = textData.text,
            style = TextStyle(
                color = textData.color,
                fontSize = textData.fontSize.sp
            )
        )
        scope.drawText(
            textLayoutResult = textLayoutResult,
            topLeft = textData.position
        )
    }

    override fun drawSelection(scope: DrawScope, selectionColor: Color, textMeasurer: TextMeasurer?) {
        val bounds = textMeasurer?.let { getBounds(it) } ?: getBounds()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
        scope.drawRect(
            color = selectionColor,
            topLeft = Offset(bounds.left, bounds.top),
            size = Size(bounds.width, bounds.height),
            style = Stroke(width = 2f, pathEffect = pathEffect)
        )
    }

    override fun containsPoint(x: Float, y: Float, textMeasurer: TextMeasurer?): Boolean {
        val bounds = textMeasurer?.let { getBounds(it) } ?: getBounds()
        return bounds.inflate(8f).contains(Offset(x, y))
    }

    override fun translate(deltaX: Float, deltaY: Float) {
        textData = textData.copy(
            position = Offset(
                textData.position.x + deltaX,
                textData.position.y + deltaY
            )
        )
    }

    override fun getBounds(): Rect {
        // Estimate based on font size (used when TextMeasurer not available)
        val estimatedWidth = (textData.text.length * textData.fontSize * 0.65f).coerceAtLeast(textData.fontSize * 0.5f)
        val estimatedHeight = textData.fontSize * 1.4f
        return Rect(
            left = textData.position.x,
            top = textData.position.y,
            right = textData.position.x + estimatedWidth,
            bottom = textData.position.y + estimatedHeight
        )
    }

    /**
     * Gets accurate bounds using TextMeasurer
     * This should be called when TextMeasurer is available
     */
    fun getBounds(textMeasurer: TextMeasurer): Rect {
        val textLayoutResult = textMeasurer.measure(
            text = textData.text,
            style = TextStyle(
                color = textData.color,
                fontSize = textData.fontSize.sp
            )
        )
        
        return Rect(
            left = textData.position.x,
            top = textData.position.y,
            right = textData.position.x + textLayoutResult.size.width,
            bottom = textData.position.y + textLayoutResult.size.height
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
        textData = textData.copy(
            position = Offset(
                textData.position.x * zoom,
                textData.position.y * zoom
            ),
            fontSize = textData.fontSize * zoom
        )
    }

    /**
     * Gets the underlying TextData
     */
    fun getTextData(): TextData = textData
}
