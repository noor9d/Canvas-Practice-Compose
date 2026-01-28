package com.imagination.canvaspractice.presentation.splash

import com.imagination.canvaspractice.core.base.BaseViewModel
import com.imagination.canvaspractice.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
): BaseViewModel<UserEvent, UiEvent>() {

    private val _isTimerCancelled = MutableStateFlow(false)

    override fun onUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.StartSplashTimer -> startSplashTimer(event.delayMillis)
            is UserEvent.CancelSplashTimer -> cancelTimer()
            is UserEvent.NavigateNext -> launchWithExceptionHandler { goToNextScreen() }
        }
    }

    private suspend fun goToNextScreen() {
        submitUIEvent(UiEvent.Navigate(Screen.Dashboard))
    }

    private fun cancelTimer() {
        println("SplashTag: Timer cancelled externally")
        _isTimerCancelled.value = true
    }

    private fun startSplashTimer(delayMillis: Long) = launchWithExceptionHandler(Dispatchers.IO) {
        // Reset cancel flag before starting
        _isTimerCancelled.value = false

        println("SplashTag: Starting splash timer for $delayMillis ms")

        var elapsed = 0L
        val interval = 1000L

        while (elapsed < delayMillis) {
            if (_isTimerCancelled.value) {
                println("SplashTag: Timer stopped at ${elapsed}ms")
                return@launchWithExceptionHandler
            }
            delay(interval)
            elapsed += interval
        }

        println("SplashTag: Timer finished after $delayMillis ms")
        goToNextScreen()
    }
}

sealed interface UiEvent {
    data class Navigate(val screen: Screen) : UiEvent
}

sealed interface UserEvent {
    data class StartSplashTimer(val delayMillis: Long) : UserEvent
    data object CancelSplashTimer : UserEvent
    data object NavigateNext : UserEvent
}