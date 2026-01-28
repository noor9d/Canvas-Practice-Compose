package com.imagination.canvaspractice.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen: NavKey {

    @Serializable
    data object Splash : Screen

    @Serializable
    data object Dashboard : Screen

    @Serializable
    data class Canvas(
        val boardId: Int
    ) : Screen

    @Serializable
    data class Note(
        val noteId: Int
    ) : Screen
}