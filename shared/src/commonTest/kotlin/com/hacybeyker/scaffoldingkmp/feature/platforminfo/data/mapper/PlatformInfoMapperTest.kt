package com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.mapper

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.model.PlatformInfoDto
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformInfoMapperTest {

    @Test
    fun `toDomain maps platformName to name`() {
        val dto = PlatformInfoDto(platformName = "Android 35")
        val result = dto.toDomain()
        assertEquals("Android 35", result.name)
    }

    @Test
    fun `toDomain generates greeting from platformName`() {
        val dto = PlatformInfoDto(platformName = "iOS 18")
        val result = dto.toDomain()
        assertEquals("Hello from iOS 18!", result.greeting)
    }
}
