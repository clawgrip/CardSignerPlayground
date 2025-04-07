package com.playground.cardsignerplayground.pages.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ManagedTextFieldState(
    val label: String,
    val value: String = "",
    val onValueChange: ((String) -> Unit),
    val error: ManagedTextFieldError? = null,
    val showError: Boolean = false,
) {
    companion object {
        fun preview(label: String) = ManagedTextFieldState(
            label = label,
            value = "",
            onValueChange = {},
            error = null,
        )
    }
}

sealed class ManagedTextFieldError {
    data object CanNotBeEmpty : ManagedTextFieldError()
    data class CustomError(val message: String) : ManagedTextFieldError()
}

@Composable
fun ManagedTextField(state: ManagedTextFieldState) {
    val error = if (state.showError)
        state.error
    else null

    TextField(
        value = state.value,
        onValueChange = state.onValueChange,
        label = { Text(state.label) },
        isError = error != null,
        supportingText = {
            if (error != null) {
                val errorMessage = when (error) {
                    ManagedTextFieldError.CanNotBeEmpty -> "This field should not be empty"
                    is ManagedTextFieldError.CustomError -> error.message
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMessage,
                )
            }
        }
    )
}
