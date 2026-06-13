package com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.mapper

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo

internal fun PlatformInfoDto.toDomain(): PlatformInfo = PlatformInfo(
    name = platformName,
    greeting = "Hello from $platformName!",
)
