package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state

sealed interface PlatformInfoEvent {
    data object LoadInfo : PlatformInfoEvent
}
