package com.hacybeyker.scaffoldingkmp.feature.home

import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.home.data.source.PlatformInfoSource

internal class FakePlatformInfoSource(private val platformInfo: PlatformInfoDto) : PlatformInfoSource {
    override fun getPlatformInfo(): PlatformInfoDto = platformInfo
}
