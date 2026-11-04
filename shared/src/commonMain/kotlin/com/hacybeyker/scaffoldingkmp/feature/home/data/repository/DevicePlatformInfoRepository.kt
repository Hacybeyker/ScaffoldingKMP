package com.hacybeyker.scaffoldingkmp.feature.home.data.repository

import com.hacybeyker.scaffoldingkmp.feature.home.data.mapper.toDomain
import com.hacybeyker.scaffoldingkmp.feature.home.data.source.PlatformInfoSource
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.home.domain.repository.PlatformInfoRepository

/** Datos del dispositivo en el que corre la app, leídos de la fuente de cada plataforma. */
internal class DevicePlatformInfoRepository(private val source: PlatformInfoSource) : PlatformInfoRepository {
    override suspend fun getPlatformInfo(): PlatformInfo = source.getPlatformInfo().toDomain()
}
