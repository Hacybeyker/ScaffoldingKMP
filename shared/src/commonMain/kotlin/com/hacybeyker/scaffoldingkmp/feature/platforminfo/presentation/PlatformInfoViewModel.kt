package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation

import androidx.lifecycle.ViewModel
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.usecase.GetPlatformInfoUseCase
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state.PlatformInfoEffect
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state.PlatformInfoEvent
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state.PlatformInfoState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class PlatformInfoViewModel(private val getPlatformInfo: GetPlatformInfoUseCase) : ViewModel() {

    private val _state = MutableStateFlow(PlatformInfoState())
    val state: StateFlow<PlatformInfoState> = _state.asStateFlow()

    private val _effect = Channel<PlatformInfoEffect>()
    val effect: Flow<PlatformInfoEffect> = _effect.receiveAsFlow()

    init {
        onEvent(PlatformInfoEvent.LoadInfo)
    }

    fun onEvent(event: PlatformInfoEvent) {
        when (event) {
            PlatformInfoEvent.LoadInfo -> loadInfo()
        }
    }

    private fun loadInfo() {
        _state.update { it.copy(isLoading = true) }
        val info = getPlatformInfo()
        _state.update { it.copy(platformInfo = info, isLoading = false) }
    }
}
