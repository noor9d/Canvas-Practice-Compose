package com.imagination.canvaspractice.presentation.dashboard

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.imagination.canvaspractice.core.base.BaseViewModel
import com.imagination.canvaspractice.data.local.database.entity.BoardEntity
import com.imagination.canvaspractice.data.mapper.BoardMapper.toDomain
import com.imagination.canvaspractice.domain.repository.BoardRepository
import com.imagination.canvaspractice.presentation.navigation.Screen
import com.imagination.canvaspractice.presentation.dashboard.model.Board
import com.imagination.canvaspractice.presentation.dashboard.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
): BaseViewModel<UserEvent, UiEvent>() {

    private val _boards = MutableStateFlow<List<Board>>(emptyList())
    val boards: StateFlow<List<Board>> = _boards.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _activeSheet = MutableStateFlow<DashboardSheet?>(null)
    val activeSheet = _activeSheet.asStateFlow()

    init {
        loadBoards()
    }

    override fun onUserEvent(event: UserEvent) {
        when (event) {
            UserEvent.AddNewBoard -> launchWithExceptionHandler { addNewBoard() }
            UserEvent.AddNewNote -> launchWithExceptionHandler { addNewNote() }
            is UserEvent.OpenBoard -> launchWithExceptionHandler { openBoard(event.boardId) }
            is UserEvent.DeleteBoard -> launchWithExceptionHandler { deleteBoard(event.boardId) }
            is UserEvent.FinishActivity -> finishActivity(event.activity)
            UserEvent.HideSheet -> _activeSheet.value = null
            is UserEvent.ShowSheet ->  _activeSheet.value = event.sheet
        }
    }

    private fun finishActivity(activity: Activity) {
        _activeSheet.value = null
        activity.finish()
    }

    private fun loadBoards() {
        viewModelScope.launch {
            boardRepository.getAllBoards().collect { boardEntities ->
                _boards.value = boardEntities.toDomain()
            }
        }
    }

    private fun addNewBoard() {
        viewModelScope.launch {
            val newBoardEntity = BoardEntity(
                id = 0, // Auto-generate
                title = "Untitled",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val boardId = boardRepository.insertBoard(newBoardEntity)
            // Navigate to the newly created board
            submitUIEvent(UiEvent.Navigate(Screen.Canvas(boardId = boardId.toInt())))
        }
    }

    private fun addNewNote() {
        viewModelScope.launch {
            val newId = (_notes.value.maxOfOrNull { it.id } ?: 0) + 1
            val newNote = Note(id = newId, title = "New Note $newId")
            _notes.value += newNote
            // Navigate to the newly created board
            submitUIEvent(UiEvent.Navigate(Screen.Note(noteId = newId)))
        }
    }

    private fun openBoard(boardId: Int) {
        launchWithExceptionHandler {
            submitUIEvent(UiEvent.Navigate(Screen.Canvas(boardId = boardId)))
        }
    }

    private fun deleteBoard(boardId: Int) {
        viewModelScope.launch {
            boardRepository.deleteBoard(boardId.toLong())
        }
    }
}

sealed interface UiEvent {
    data class Navigate(val screen: Screen) : UiEvent
}

sealed interface UserEvent {
    data object AddNewBoard : UserEvent
    data object AddNewNote : UserEvent
    data class OpenBoard(val boardId: Int) : UserEvent
    data class DeleteBoard(val boardId: Int) : UserEvent
    data class FinishActivity(val activity: Activity) : UserEvent
    data class ShowSheet(val sheet: DashboardSheet) : UserEvent
    data object HideSheet : UserEvent
}

enum class DashboardSheet {
    EXIT_SHEET,
    BOARD_OPTION_SHEET
}