package com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.usecase

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository.PlatformInfoRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetPlatformInfoUseCaseTest {

    private val fakePlatformInfo = PlatformInfo(
        name = "Test Platform",
        greeting = "Hello from Test Platform!",
    )

    private val fakeRepository = object : PlatformInfoRepository {
        override fun getPlatformInfo(): PlatformInfo = fakePlatformInfo
    }

    private val useCase = GetPlatformInfoUseCase(fakeRepository)

    @Test
    fun `invoke returns platform info from repository`() {
        val result = useCase()
        assertNotNull(result)
        assertEquals(fakePlatformInfo.name, result.name)
        assertEquals(fakePlatformInfo.greeting, result.greeting)
    }

    @Test
    fun `invoke delegates to repository exactly once`() {
        var callCount = 0
        val countingRepository = object : PlatformInfoRepository {
            override fun getPlatformInfo(): PlatformInfo {
                callCount++
                return fakePlatformInfo
            }
        }

        GetPlatformInfoUseCase(countingRepository).invoke()

        assertEquals(1, callCount)
    }
}
