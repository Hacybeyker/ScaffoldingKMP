package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation

import app.cash.turbine.test
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository.PlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.usecase.GetPlatformInfoUseCase
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state.PlatformInfoEvent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class PlatformInfoViewModelTest {

    private val fakePlatformInfo = PlatformInfo(
        name = "Test Platform",
        greeting = "Hello from Test Platform!",
    )

    private val fakeRepository = object : PlatformInfoRepository {
        override fun getPlatformInfo(): PlatformInfo = fakePlatformInfo
    }

    private val useCase = GetPlatformInfoUseCase(fakeRepository)

    @Test
    fun `init auto-loads platform info`() = runTest {
        val viewModel = PlatformInfoViewModel(useCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            val info = assertNotNull(state.platformInfo)
            assertEquals(fakePlatformInfo.name, info.name)
            assertEquals(fakePlatformInfo.greeting, info.greeting)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadInfo event produces loaded state with correct data`() = runTest {
        // Use a mutable fake to verify state after explicit onEvent
        var returnedInfo = fakePlatformInfo
        val mutableRepository = object : PlatformInfoRepository {
            override fun getPlatformInfo(): PlatformInfo = returnedInfo
        }
        val viewModel = PlatformInfoViewModel(GetPlatformInfoUseCase(mutableRepository))

        val updatedInfo = PlatformInfo(name = "Updated Platform", greeting = "Hello from Updated Platform!")
        returnedInfo = updatedInfo

        viewModel.onEvent(PlatformInfoEvent.LoadInfo)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(updatedInfo.name, state.platformInfo?.name)
            assertEquals(updatedInfo.greeting, state.platformInfo?.greeting)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
