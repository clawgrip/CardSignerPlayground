package com.accenture.cardsignerplayground.pages.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface UIEvent {
    val timestamp: Long
}

class UIEventEmitter<T : UIEvent> {
    private val _events = MutableSharedFlow<T>()

    suspend fun emit(event: T) {
        _events.emit(event)
    }

    fun receiver(): UIEventReceiver<T> {
        return UIEventReceiver(_events.asSharedFlow())
    }
}

class UIEventReceiver<T : UIEvent>(val events: SharedFlow<T>)


@Composable
fun <T : UIEvent> LaunchedEffectViewModelUIEvent(
    receiver: UIEventReceiver<T>,
    initialValue: T,
    block: (T) -> Unit,
) {
    val current by receiver.events.collectAsStateWithLifecycle(initialValue)

    var lastTimestamp by rememberSaveable { mutableLongStateOf(-1L) }
    LaunchedEffect(current) {
        if (lastTimestamp == current.timestamp) {
            return@LaunchedEffect
        }
        lastTimestamp = current.timestamp

        block(current)
    }
}
