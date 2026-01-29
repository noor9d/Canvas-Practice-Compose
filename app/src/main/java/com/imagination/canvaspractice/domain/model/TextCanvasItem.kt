package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    override fun drawSelection(scope: DrawScope) {
        val bounds = getBounds()
        // Draw selection rectangle around the text
        scope.drawRect(
            color = textData.color.copy(alpha = 0.3f),
            topLeft = Offset(bounds.left, bounds.top),
            size = androidx.compose.ui.geometry.Size(bounds.width, bounds.height)
        )
    }

    override fun containsPoint(x: Float, y: Float): Boolean {
        val bounds = getBounds()
        return bounds.contains(Offset(x, y))
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
        // For text, we need to measure it to get accurate bounds
        // Since we don't have TextMeasurer here, we'll estimate based on font size
        // In practice, this should be calculated when the item is created or cached
        val estimatedWidth = textData.text.length * textData.fontSize * 0.6f // Rough estimate
        val estimatedHeight = textData.fontSize * 1.2f // Rough estimate
        
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
