package com.accenture.cardsignerplayground.pages.home

import android.nfc.Tag
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.accenture.cardsignerplayground.nfc.NfcScanningManager
import com.accenture.cardsignerplayground.pages.common.ManagedButtonState
import com.accenture.cardsignerplayground.pages.common.ManagedTextFieldError
import com.accenture.cardsignerplayground.pages.common.ManagedTextFieldState

class HomeVMImpl : HomeVM() {

    //region Properties
    override var ui by mutableStateOf(HomeUI.PREVIEW)
    private var nfcScanningManager: NfcScanningManager? = null // if set is means is scanning
    //endregion

    //region Lifecycle

    override fun onCreate() {
        super.onCreate()

        ui = HomeUI(
            can = ManagedTextFieldState(
                label = ui.can.label,
                onValueChange = this::onCANValueChanged,
            ),
            pin = ManagedTextFieldState(
                label = ui.pin.label,
                onValueChange = this::onPINValueChanged,
            ),
            scanButton = ManagedButtonState(
                title = ui.scanButton.title,
                onClick = this::onSubmitButtonClicked,
                enabled = false
            ),
            infoCardText = null,
        )
    }
    //endregion

    //region Event handlers

    override fun onCANValueChanged(text: String) {
        val fieldError = validateCAN(text)
        ui = ui.copy(
            can = ui.can.copy(
                value = text,
                error = fieldError
            )
        )

        updateScanButtonState()
    }


    override fun onPINValueChanged(text: String) {
        ui = ui.copy(
            pin = ui.pin.copy(
                value = text,
                error = validatePIN(text)
            )
        )
    }

    override fun onSubmitButtonClicked() {
        if (nfcScanningManager != null) {
            Log.i(TAG, "onSubmitButtonClicked: while scanning already started")
            return
        }
        Log.i(TAG, "onSubmitButtonClicked: starting scan")

        ui = ui.copy(
            infoCardText = "Starting scan..."
        )

        nfcScanningManager = NfcScanningManager(can = ui.can.value, pin = ui.pin.value)
    }

    override fun onTagRead(tag: Tag) {
        val manager = nfcScanningManager
        if (manager == null) {
            Log.w(TAG, "onTagRead: tag detected while not reading")
            return
        }

        ui = ui.copy(
            infoCardText = "Card has been detected!! See logcat to keep track of status."
        )

        manager.onTagRead(tag)
    }
    //endregion

    //region Workers

    private fun validateCAN(text: String): ManagedTextFieldError? {
        if (text.isEmpty())
            return ManagedTextFieldError.CanNotBeEmpty

        if (text.length != 6)
            return ManagedTextFieldError.CustomError("La longitud del CAN tiene que ser 6")

        return null
    }

    private fun validatePIN(text: String): ManagedTextFieldError? {
        if (text.isEmpty())
            return ManagedTextFieldError.CanNotBeEmpty

        return null
    }

    private fun updateScanButtonState() {
        val firstFieldWithError = listOf(ui.can, ui.pin).firstOrNull {
            it.error != null
        }

        ui = ui.copy(
            scanButton = ui.scanButton.copy(
                enabled = firstFieldWithError == null
            )
        )
    }
    //endregion

    companion object {
        private const val TAG = "HomeVMImpl"
    }
}