package com.hacybeyker.scaffoldingkmp.feature.home.data.source

import android.os.Build
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidPlatformInfoSourceTest {

    @Test
    fun `reports Android and the API level of the device`() {
        val platformInfo = AndroidPlatformInfoSource().getPlatformInfo()

        assertEquals("Android", platformInfo.systemName)
        assertEquals(Build.VERSION.SDK_INT.toString(), platformInfo.systemVersion)
    }
}
