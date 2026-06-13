package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo

data class PlatformInfoState(val platformInfo: PlatformInfo? = null, val isLoading: Boolean = false)
