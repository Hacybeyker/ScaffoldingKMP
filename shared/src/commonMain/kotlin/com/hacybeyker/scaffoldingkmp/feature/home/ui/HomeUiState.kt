package com.hacybeyker.scaffoldingkmp.feature.home.ui

import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo

data class HomeUiState(val isLoading: Boolean = true, val platformInfo: PlatformInfo? = null)
