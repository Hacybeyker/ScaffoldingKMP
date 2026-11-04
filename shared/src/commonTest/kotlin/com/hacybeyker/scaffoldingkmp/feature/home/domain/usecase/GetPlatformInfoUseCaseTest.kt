package com.hacybeyker.scaffoldingkmp.feature.home.domain.usecase

import com.hacybeyker.scaffoldingkmp.feature.home.FakePlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPlatformInfoUseCaseTest {

    private val repository = FakePlatformInfoRepository()
    private val useCase = GetPlatformInfoUseCase(repository)

    @Test
    fun `returns the platform info of the repository`() = runTest {
        repository.platformInfo = PlatformInfo(name = "iOS", version = "26.0")

        assertEquals(PlatformInfo(name = "iOS", version = "26.0"), useCase())
    }
}
