package com.hacybeyker.scaffoldingkmp.feature.home.domain.repository

import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo

interface PlatformInfoRepository {
    suspend fun getPlatformInfo(): PlatformInfo
}
