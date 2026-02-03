package com.imagination.canvaspractice.domain.model

/**
 * Represents a selected item on the canvas for selection and move operations.
 */
sealed class SelectedItem {
    abstract val id: String

    data class PathItem(override val id: String) : SelectedItem()
    data class ShapeItem(override val id: String) : SelectedItem()
    data class TextItem(override val id: String) : SelectedItem()
}
