package com.accenture.cardsignerplayground.pages.common

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<UI, Event : UIEvent> : ViewModel() {
    abstract val ui: UI

    private val _uiEvents = UIEventEmitter<Event>()
    val uiEvents = _uiEvents.receiver()

    open fun onCreate() {}
}
