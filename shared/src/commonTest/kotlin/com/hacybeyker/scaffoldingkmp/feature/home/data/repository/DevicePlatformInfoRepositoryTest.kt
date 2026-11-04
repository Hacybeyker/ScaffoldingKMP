package com.hacybeyker.scaffoldingkmp.feature.home.data.repository

import com.hacybeyker.scaffoldingkmp.feature.home.FakePlatformInfoSource
import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicePlatformInfoRepositoryTest {

    @Test
    fun `returns the platform info of the source as a domain model`() = runTest {
        val source = FakePlatformInfoSource(PlatformInfoDto(systemName = "iOS", systemVersion = "26.0"))
        val repository = DevicePlatformInfoRepository(source)

        assertEquals(PlatformInfo(name = "iOS", version = "26.0"), repository.getPlatformInfo())
    }
}
