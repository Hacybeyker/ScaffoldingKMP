package com.hacybeyker.scaffoldingkmp.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hacybeyker.scaffoldingkmp.feature.home.domain.usecase.GetPlatformInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val getPlatformInfo: GetPlatformInfoUseCase) : ViewModel() {

    private val state = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = state
        .onStart { loadPlatformInfo() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState(),
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.Refresh -> viewModelScope.launch { loadPlatformInfo() }
        }
    }

    private suspend fun loadPlatformInfo() {
        state.update { it.copy(isLoading = true) }
        val platformInfo = getPlatformInfo()
        state.update { it.copy(isLoading = false, platformInfo = platformInfo) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
