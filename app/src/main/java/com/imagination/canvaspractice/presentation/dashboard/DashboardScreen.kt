package com.imagination.canvaspractice.presentation.dashboard

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imagination.canvaspractice.core.components.GenericTopBar
import com.imagination.canvaspractice.presentation.dashboard.components.AddNewBoardItem
import com.imagination.canvaspractice.presentation.dashboard.components.BoardItem
import com.imagination.canvaspractice.presentation.navigation.BackStack
import com.imagination.canvaspractice.presentation.navigation.push
import com.imagination.canvaspractice.presentation.sheets.BoardOption
import com.imagination.canvaspractice.presentation.sheets.BoardOptionsBottomSheet
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import com.synapses.presentation.dashboard.model.Board

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    backStack: BackStack
) {
    val activity = LocalActivity.current
    val boards by viewModel.boards.collectAsState()
    var selectedBoard by remember { mutableStateOf<Board?>(null) }
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    backStack.push(event.screen)
                }
            }
        }
    }

    BackHandler {
        viewModel.registerUserEvent(UserEvent.ShowSheet(DashboardSheet.EXIT_SHEET))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GenericTopBar(
                modifier = Modifier.background(CanvasPracticeTheme.colorScheme.primary),
                title = "Dashboard",
                titleColor = Color.White,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CanvasPracticeTheme.colorScheme.background),
            contentPadding = PaddingValues(CanvasPracticeTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(CanvasPracticeTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(CanvasPracticeTheme.spacing.medium)
        ) {
            // First item - Add New Board
            item {
                AddNewBoardItem(
                    title = "New Canvas",
                    onClick = {
                        viewModel.registerUserEvent(UserEvent.AddNewBoard)
                    }
                )
            }

            // Board items
            itemsIndexed(boards) { _, board ->
                BoardItem(
                    board = board,
                    onClick = {
                        viewModel.registerUserEvent(
                            UserEvent.OpenBoard(board.id)
                        )
                    },
                    onOptionsClick = {
                        selectedBoard = board
                        viewModel.registerUserEvent(UserEvent.ShowSheet(DashboardSheet.BOARD_OPTION_SHEET))
                    }
                )
            }
        }
    }

    if (activeSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.registerUserEvent(UserEvent.HideSheet) },
            sheetState = bottomSheetState,
            containerColor = CanvasPracticeTheme.colorScheme.surface
        ) {
            when (activeSheet) {
                DashboardSheet.EXIT_SHEET -> {
                    /*ExitBottomSheet(
                        onExitClicked = {
                            activity?.let {
                                viewModel.registerUserEvent(UserEvent.FinishActivity(it))
                            }
                        },
                        onDismiss = { viewModel.registerUserEvent(UserEvent.HideSheet) }
                    )*/
                }

                DashboardSheet.BOARD_OPTION_SHEET -> {
                    if (selectedBoard != null) {
                        BoardOptionsBottomSheet(
                            onDismiss = { viewModel.registerUserEvent(UserEvent.HideSheet) },
                            onOptionSelected = { option ->
                                selectedBoard?.let { board ->
                                    when (option) {
                                        is BoardOption.Delete -> {
                                            viewModel.registerUserEvent(UserEvent.DeleteBoard(board.id))
                                        }

                                        is BoardOption.Share -> {
                                            // TODO: Implement share functionality
                                        }

                                        is BoardOption.CopyLink -> {
                                            // TODO: Implement copy link functionality
                                        }

                                        is BoardOption.AddToFavourite -> {
                                            // TODO: Implement add to favourite functionality
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                else -> {}
            }
        }
    }
}