package com.imagination.canvaspractice.domain.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer

/**
 * Interface for all canvas-drawable items
 * Follows SOLID principles:
 * - Single Responsibility: Each implementation handles one type of item
 * - Open/Closed: New item types can be added without modifying existing code
 * - Liskov Substitution: All implementations can be used interchangeably
 * - Interface Segregation: Only contains methods relevant to all canvas items
 * - Dependency Inversion: Canvas depends on this abstraction, not concrete implementations
 */
interface CanvasItem {
    /**
     * Draws the item on the canvas
     * @param scope The drawing scope to draw on
     * @param textMeasurer Text measurer for text-related items
     */
    fun draw(scope: DrawScope, textMeasurer: TextMeasurer)

    /**
     * Draws selection indicators around the item
     * @param scope The drawing scope to draw on
     */
    fun drawSelection(scope: DrawScope)

    /**
     * Checks if a point is contained within this item
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @return true if the point is within the item's bounds
     */
    fun containsPoint(x: Float, y: Float): Boolean

    /**
     * Translates (moves) the item by the given delta
     * @param deltaX X offset to move
     * @param deltaY Y offset to move
     */
    fun translate(deltaX: Float, deltaY: Float)

    /**
     * Gets the bounding rectangle of the item
     * @return Rect representing the item's bounds
     */
    fun getBounds(): Rect

    /**
     * Checks if the item intersects with the given rectangle
     * @param rect Rectangle to check intersection with
     * @return true if the item intersects with the rectangle
     */
    fun intersects(rect: Rect): Boolean

    /**
     * Checks if the item is visible in the given viewport
     * @param viewport The viewport rectangle
     * @return true if the item should be drawn (is visible)
     */
    fun isVisible(viewport: Rect): Boolean

    /**
     * Resizes the item by the given zoom factor
     * @param zoom Zoom factor (1.0 = no change, >1.0 = larger, <1.0 = smaller)
     */
    fun resize(zoom: Float)
}
