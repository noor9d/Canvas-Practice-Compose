package com.imagination.canvaspractice.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.imagination.canvaspractice.presentation.MainViewModel
import com.imagination.canvaspractice.presentation.canvas.CanvasScreen
import com.imagination.canvaspractice.presentation.canvas.CanvasViewModel
import com.imagination.canvaspractice.presentation.dashboard.DashboardScreen
import com.imagination.canvaspractice.presentation.dashboard.DashboardViewModel
import com.imagination.canvaspractice.presentation.splash.SplashScreen
import com.imagination.canvaspractice.presentation.splash.SplashViewModel
import kotlin.collections.removeLastOrNull

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val backStack = viewModel.backStack

    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        NavDisplay(
            modifier = modifier.semantics { testTagsAsResourceId = true },
            transitionSpec = {
                // Slide in from right when navigating forward
                slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it })
            },
            popTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { it })
            },
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Screen.Splash> {
                    val splashViewModel = hiltViewModel<SplashViewModel>()
                    SplashScreen(
                        viewModel = splashViewModel,
                        backStack = backStack
                    )
                }
                entry<Screen.Dashboard> {
                    val dashboardViewModel = hiltViewModel<DashboardViewModel>()
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        backStack = backStack
                    )
                }
                entry<Screen.Canvas> { key ->
                    val canvasViewModel = hiltViewModel<CanvasViewModel>()
                    CanvasScreen(
                        boardId = key.boardId,
                        viewModel = canvasViewModel,
                        backStack = backStack
                    )
                }
            }
        )
    }
}