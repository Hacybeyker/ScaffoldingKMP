package com.hacybeyker.scaffoldingkmp.feature.home.domain.usecase

import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.home.domain.repository.PlatformInfoRepository

class GetPlatformInfoUseCase(private val repository: PlatformInfoRepository) {
    suspend operator fun invoke(): PlatformInfo = repository.getPlatformInfo()
}
