package com.playground.cardsignerplayground.pages.home

import android.annotation.SuppressLint
import android.nfc.Tag
import com.playground.cardsignerplayground.pages.common.BaseViewModel
import com.playground.cardsignerplayground.pages.common.ManagedButtonState
import com.playground.cardsignerplayground.pages.common.ManagedTextFieldState
import com.playground.cardsignerplayground.pages.common.UIEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class HomeVM : BaseViewModel<HomeUI, HomeEvent>() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        fun createPreview(): HomeVM = object : HomeVM() {
            override val shouldListenNFCTags: StateFlow<Boolean> =
                MutableStateFlow(false).asStateFlow()

            override fun createInitialUI() = HomeUI.PREVIEW
            override fun onCANValueChanged(text: String) {}
            override fun onPINValueChanged(text: String) {}
            override fun onSubmitButtonClicked() {}
            override fun onTagRead(tag: Tag) {}
        }
    }

    abstract val shouldListenNFCTags: Flow<Boolean>

    abstract fun onCANValueChanged(text: String)
    abstract fun onPINValueChanged(text: String)
    abstract fun onSubmitButtonClicked()
    abstract fun onTagRead(tag: Tag)
}

sealed class HomeEvent(
    override val timestamp: Long = System.currentTimeMillis(),
) : UIEvent {
    data object NONE : HomeEvent(timestamp = 0L)
}

data class HomeUI(
    val can: ManagedTextFieldState,
    val pin: ManagedTextFieldState,
    val scanButton: ManagedButtonState,
    val infoCardText: String?,
    val isReadingTag: Boolean,
) {
    companion object {
        val PREVIEW = HomeUI(
            can = ManagedTextFieldState.preview("Introduce CAN"),
            pin = ManagedTextFieldState.preview("Introduce PIN"),
            scanButton = ManagedButtonState.sample("Escanear"),
            infoCardText = null,
            isReadingTag = false,
        )
    }
}
