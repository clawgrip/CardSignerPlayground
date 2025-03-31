package com.accenture.cardsignerplayground.pages.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun VMStandardEffects(vm: BaseViewModel<*, *>) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        vm.onCreate()
    }
}
