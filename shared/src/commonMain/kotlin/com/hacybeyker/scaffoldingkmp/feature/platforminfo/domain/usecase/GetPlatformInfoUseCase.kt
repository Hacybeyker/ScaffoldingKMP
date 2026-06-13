package com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.usecase

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository.PlatformInfoRepository

class GetPlatformInfoUseCase(private val repository: PlatformInfoRepository) {
    operator fun invoke(): PlatformInfo = repository.getPlatformInfo()
}
