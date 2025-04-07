package com.playground.cardsignerplayground.pages.home

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.playground.cardsignerplayground.nfc.NfcScanningManager
import com.playground.cardsignerplayground.pages.common.ManagedButtonState
import com.playground.cardsignerplayground.pages.common.ManagedTextFieldError
import com.playground.cardsignerplayground.pages.common.ManagedTextFieldState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val TAG = "HomeVMImpl"

class HomeVMImpl : HomeVM() {

    //region Properties
    private val nfcScanningManagerIsSet = MutableStateFlow(false)
    private var nfcScanningManager: NfcScanningManager? = null // if set is means is scanning

    override val shouldListenNFCTags: Flow<Boolean>
    //endregion

    //region Lifecycle
    init {
        shouldListenNFCTags = nfcScanningManagerIsSet
            .combine(uiState) { scanningManagerIsSet, ui ->
                scanningManagerIsSet && !ui.isReadingTag
            }
    }

    override fun createInitialUI() = HomeUI(
        can = ManagedTextFieldState(
            label = HomeUI.PREVIEW.can.label,
            onValueChange = this::onCANValueChanged,
            error = validateCAN(HomeUI.PREVIEW.can.value)
        ),
        pin = ManagedTextFieldState(
            label = HomeUI.PREVIEW.pin.label,
            onValueChange = this::onPINValueChanged,
            error = validatePIN(HomeUI.PREVIEW.pin.value)
        ),
        scanButton = ManagedButtonState(
            title = HomeUI.PREVIEW.scanButton.title,
            onClick = this::onSubmitButtonClicked,
            enabled = true
        ),
        infoCardText = null,
        isReadingTag = false,
    )
    //endregion

    //region Event handlers

    override fun onCANValueChanged(text: String) {
        updateUI {
            it.copy(
                can = it.can.copy(
                    value = text,
                    error = validateCAN(text),
                    showError = false,
                )
            )
        }
    }

    override fun onPINValueChanged(text: String) {
        updateUI {
            it.copy(
                pin = it.pin.copy(
                    value = text,
                    error = validatePIN(text),
                    showError = false,
                )
            )
        }
    }

    override fun onSubmitButtonClicked() {
        if (nfcScanningManager != null) {
            Log.i(TAG, "onSubmitButtonClicked: while scanning already started")
            return
        }
        if (evaluateCanScan() == false) {
            Log.i(TAG, "onSubmitButtonClicked: cannot scan due to erros in fields")
            return
        }

        Log.i(TAG, "onSubmitButtonClicked: starting scan")

        updateUI {
            it.copy(
                infoCardText = "Starting scan..."
            )
        }

        nfcScanningManager = NfcScanningManager(can = ui.can.value, pin = ui.pin.value)
        nfcScanningManagerIsSet.tryEmit(true)
    }

    override fun onTagRead(tag: Tag) {
        val manager = nfcScanningManager
        if (manager == null) {
            Log.w(TAG, "onTagRead: tag detected while not reading")
            return
        }

        viewModelScope.launch {
            updateUI {
                it.copy(
                    infoCardText = "Card has been detected!! See logcat to keep track of status.", // FIX: not shown due to activity state is set to paused while reading tag
                    isReadingTag = true,
                )
            }

            manager.onTagRead(tag)

            nfcScanningManager = null
            nfcScanningManagerIsSet.tryEmit(false)
            updateUI {
                it.copy(
                    infoCardText = null,
                    isReadingTag = false,
                )
            }
        }
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

    private fun evaluateCanScan(): Boolean {
        val firstFieldWithError = listOf(ui.can, ui.pin).firstOrNull {
            it.error != null
        }

        val noErrors = firstFieldWithError == null

        updateUI {
            it.copy(
                can = it.can.copy(
                    showError = true
                ),
                pin = it.pin.copy(
                    showError = true
                )
            )
        }

        return noErrors
    }
    //endregion
}