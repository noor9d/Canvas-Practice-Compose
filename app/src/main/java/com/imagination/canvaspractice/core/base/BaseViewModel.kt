package com.imagination.canvaspractice.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<UserEvent, UIEvent>(replay: Int = 0) :
    UIEventListener<UIEvent>, ViewModel() {

    private val _uiEvent: MutableSharedFlow<UIEvent> = MutableSharedFlow(replay = replay)

    @OptIn(FlowPreview::class)
    val uiEvent = _uiEvent.asSharedFlow().debounce(500L)

    override suspend fun submitUIEvent(uiEvent: UIEvent) {
        println("UIEvent ${this::class.simpleName} <-- $uiEvent")
        _uiEvent.emit(uiEvent)
    }


    fun registerUserEvent(event: UserEvent) {
        println("UserEvent ${this::class.simpleName} --> $event")
        onUserEvent(event)
    }

    protected abstract fun onUserEvent(event: UserEvent)

    protected fun launchWithExceptionHandler(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context + CoroutineExceptionHandler { coroutineContext, throwable ->
        // Log the exception with details about the coroutine context
        val contextName = coroutineContext[CoroutineName]?.name ?: "Unknown"
        println("Error ${this::class.simpleName} in coroutine $contextName: $throwable")
        // To get the exact line of code, you can inspect the stack trace
        throwable.stackTrace.firstOrNull()?.let {
            println("Exception occurred at: ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})")
        }
        throwable.printStackTrace() // Optional for detailed stack trace
    }, start, block)
}

fun interface UIEventListener<UIEvent> {
    suspend fun submitUIEvent(uiEvent: UIEvent)
}