package com.hacybeyker.scaffoldingkmp.feature.home

import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.home.domain.repository.PlatformInfoRepository

/** Cumple el contrato de [PlatformInfoRepository] con datos en memoria que el test puede cambiar. */
class FakePlatformInfoRepository(var platformInfo: PlatformInfo = PlatformInfo(name = "Android", version = "37")) :
    PlatformInfoRepository {
    override suspend fun getPlatformInfo(): PlatformInfo = platformInfo
}
