package com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo

interface PlatformInfoRepository {
    fun getPlatformInfo(): PlatformInfo
}
