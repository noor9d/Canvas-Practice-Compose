package com.imagination.canvaspractice.domain.model

/**
 * Factory for creating CanvasItem instances from domain models
 * Follows Factory Pattern and Open/Closed Principle
 */
object CanvasItemFactory {
    /**
     * Creates a CanvasItem from PathData
     */
    fun createPathItem(pathData: PathData): CanvasItem {
        return PathCanvasItem(pathData)
    }

    /**
     * Creates a CanvasItem from ShapeData
     */
    fun createShapeItem(shapeData: ShapeData): CanvasItem {
        return ShapeCanvasItem(shapeData)
    }

    /**
     * Creates a CanvasItem from TextData
     */
    fun createTextItem(textData: TextData): CanvasItem {
        return TextCanvasItem(textData)
    }

    /**
     * Creates a list of CanvasItems from domain models
     */
    fun createItems(
        paths: List<PathData>,
        shapes: List<ShapeData>,
        texts: List<TextData>
    ): List<CanvasItem> {
        val items = mutableListOf<CanvasItem>()
        items.addAll(paths.map { createPathItem(it) })
        items.addAll(shapes.map { createShapeItem(it) })
        items.addAll(texts.map { createTextItem(it) })
        return items
    }
}
