package com.hacybeyker.scaffoldingkmp.feature.home.data.source

import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto
import platform.UIKit.UIDevice

internal class IosPlatformInfoSource : PlatformInfoSource {
    override fun getPlatformInfo(): PlatformInfoDto {
        val device = UIDevice.currentDevice
        return PlatformInfoDto(systemName = device.systemName(), systemVersion = device.systemVersion)
    }
}
