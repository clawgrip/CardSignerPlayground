package com.playground.cardsignerplayground.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playground.cardsignerplayground.pages.common.LaunchedEffectViewModelUIEvent
import com.playground.cardsignerplayground.pages.common.ManagedButton
import com.playground.cardsignerplayground.pages.common.ManagedTextField
import com.playground.cardsignerplayground.pages.common.VMStandardEffects
import com.playground.cardsignerplayground.pages.common.swallowClicks
import com.playground.cardsignerplayground.ui.theme.MyAppTheme

@Composable
fun HomePage(vm: HomeVM) {
    VMStandardEffects(vm)

    LaunchedEffectViewModelUIEvent(vm.uiEvents, HomeEvent.NONE) {
        when (it) {
            HomeEvent.NONE -> Unit
        }
    }

    val ui by vm.uiState.collectAsStateWithLifecycle()

    Box {
        Scaffold(
            modifier = Modifier
                .padding(16.dp)
                .zIndex(1f)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ManagedTextField(ui.can)
                ManagedTextField(ui.pin)
                ManagedButton(ui.scanButton)
            }
        }

        val infoCardText = ui.infoCardText
        if (infoCardText != null) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .swallowClicks()
                    .zIndex(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .size(width = 240.dp, height = 100.dp)
                ) {
                    Text(
                        text = infoCardText,
                        modifier = Modifier
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MyAppTheme {
        HomePage(vm = HomeVM.createPreview())
    }
}