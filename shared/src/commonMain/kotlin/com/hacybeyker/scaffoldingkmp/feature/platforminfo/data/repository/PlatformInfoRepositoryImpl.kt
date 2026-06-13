package com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.repository

import com.hacybeyker.scaffoldingkmp.Platform
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.mapper.toDomain
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository.PlatformInfoRepository

internal class PlatformInfoRepositoryImpl(private val platform: Platform) : PlatformInfoRepository {
    override fun getPlatformInfo(): PlatformInfo = PlatformInfoDto(platformName = platform.name).toDomain()
}
