package com.accenture.cardsignerplayground.pages.home

import android.annotation.SuppressLint
import android.nfc.Tag
import com.accenture.cardsignerplayground.pages.common.BaseViewModel
import com.accenture.cardsignerplayground.pages.common.ManagedButtonState
import com.accenture.cardsignerplayground.pages.common.ManagedTextFieldState
import com.accenture.cardsignerplayground.pages.common.UIEvent

abstract class HomeVM : BaseViewModel<HomeUI, HomeEvent>() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        fun createPreview(): HomeVM = object : HomeVM() {
            override val ui: HomeUI = HomeUI.PREVIEW

            override fun onCANValueChanged(text: String) {}
            override fun onPINValueChanged(text: String) {}
            override fun onSubmitButtonClicked() {}
            override fun onTagRead(tag: Tag) {}
        }
    }

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
) {
    companion object {
        val PREVIEW = HomeUI(
            can = ManagedTextFieldState.preview("Introduce CAN"),
            pin = ManagedTextFieldState.preview("Introduce PIN"),
            scanButton = ManagedButtonState.sample("Escanear"),
            infoCardText = null,
        )
    }
}
