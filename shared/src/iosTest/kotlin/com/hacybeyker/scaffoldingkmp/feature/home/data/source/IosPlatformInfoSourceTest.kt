package com.hacybeyker.scaffoldingkmp.feature.home.data.source

import platform.UIKit.UIDevice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IosPlatformInfoSourceTest {

    @Test
    fun `reports the system name and version of the device`() {
        val platformInfo = IosPlatformInfoSource().getPlatformInfo()

        assertEquals(UIDevice.currentDevice.systemName(), platformInfo.systemName)
        assertEquals(UIDevice.currentDevice.systemVersion, platformInfo.systemVersion)
        assertTrue(platformInfo.systemVersion.isNotBlank())
    }
}
