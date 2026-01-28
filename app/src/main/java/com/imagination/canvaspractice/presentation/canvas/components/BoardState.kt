package com.imagination.canvaspractice.presentation.canvas.components

import com.synapses.presentation.dashboard.model.Board

/**
 * Represents the state of the board/canvas loading.
 */
sealed class BoardState {
    /**
     * Board is currently loading
     */
    data object Loading : BoardState()

    /**
     * An error occurred while loading the board
     * @param message Error message to display
     */
    data class Error(val message: String) : BoardState()

    /**
     * Board content is loaded and ready
     * @param board The board information
     * @param pageData The page/canvas data
     */
    data class Content(
        val board: Board
    ) : BoardState()
}