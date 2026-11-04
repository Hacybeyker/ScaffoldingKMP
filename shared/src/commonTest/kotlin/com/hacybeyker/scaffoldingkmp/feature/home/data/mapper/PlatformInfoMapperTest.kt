package com.hacybeyker.scaffoldingkmp.feature.home.data.mapper

import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformInfoMapperTest {

    @Test
    fun `maps the system name and version to the domain model`() {
        val dto = PlatformInfoDto(systemName = "Android", systemVersion = "37")

        assertEquals(PlatformInfo(name = "Android", version = "37"), dto.toDomain())
    }
}
