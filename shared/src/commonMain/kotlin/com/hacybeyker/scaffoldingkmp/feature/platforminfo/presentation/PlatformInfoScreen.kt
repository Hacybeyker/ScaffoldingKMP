package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlatformInfoScreen(viewModel: PlatformInfoViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlatformInfoContent(state = state)
}
