package com.hacybeyker.scaffoldingkmp.feature.home.data.mapper

import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo

internal fun PlatformInfoDto.toDomain(): PlatformInfo = PlatformInfo(name = systemName, version = systemVersion)
