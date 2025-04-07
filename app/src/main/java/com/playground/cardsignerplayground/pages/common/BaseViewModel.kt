package com.playground.cardsignerplayground.pages.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<UI, Event : UIEvent> : ViewModel() {

    private val _uiState: MutableStateFlow<UI> by lazy { MutableStateFlow(createInitialUI()) }
    val uiState = _uiState.asStateFlow()
    protected val ui: UI get() = uiState.value

    private val _uiEvents = UIEventEmitter<Event>()
    val uiEvents = _uiEvents.receiver()

    open fun onCreate() {}
    abstract fun createInitialUI(): UI

    fun updateUI(block: (UI) -> UI) {
        val updated = block(uiState.value)
        viewModelScope.launch {
            _uiState.emit(updated)
        }
    }
}
