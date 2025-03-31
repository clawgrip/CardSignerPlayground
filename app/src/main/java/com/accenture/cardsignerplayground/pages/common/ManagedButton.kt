package com.accenture.cardsignerplayground.pages.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class ManagedButtonState(
    val title: String = "",
    val onClick: (() -> Unit),
    val enabled: Boolean = true,
) {
    companion object {
        fun sample(title: String) = ManagedButtonState(
            title = title,
            onClick = {},
            enabled = false
        )
    }
}

@Composable
fun ManagedButton(state: ManagedButtonState) {
    Button(
        onClick = state.onClick,
        enabled = state.enabled
    ) {
        Text(state.title)
    }
}