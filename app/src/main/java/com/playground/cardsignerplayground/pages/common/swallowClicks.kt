package com.playground.cardsignerplayground.pages.common

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.swallowClicks(): Modifier {
    return this.clickable(
        enabled = false,
        onClickLabel = "swallowClicks",
        onClick = {}
    )
}
