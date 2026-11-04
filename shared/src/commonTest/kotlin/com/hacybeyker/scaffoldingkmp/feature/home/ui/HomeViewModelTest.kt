package com.hacybeyker.scaffoldingkmp.feature.home.ui

import app.cash.turbine.test
import com.hacybeyker.scaffoldingkmp.core.testing.runTestWithMainDispatcher
import com.hacybeyker.scaffoldingkmp.feature.home.FakePlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.home.domain.usecase.GetPlatformInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val repository = FakePlatformInfoRepository(PlatformInfo(name = "Android", version = "37"))

    // Se crea dentro de cada test: viewModelScope toma Dispatchers.Main al construir el ViewModel.
    private fun buildViewModel() = HomeViewModel(GetPlatformInfoUseCase(repository))

    @Test
    fun `starts loading and then shows the platform info`() = runTestWithMainDispatcher {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            assertEquals(HomeUiState(isLoading = true, platformInfo = null), awaitItem())
            assertEquals(
                HomeUiState(isLoading = false, platformInfo = PlatformInfo(name = "Android", version = "37")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `refresh shows the latest platform info`() = runTestWithMainDispatcher {
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            skipItems(2)
            repository.platformInfo = PlatformInfo(name = "Android", version = "38")

            viewModel.onAction(HomeAction.Refresh)
            advanceUntilIdle()

            assertEquals(
                HomeUiState(isLoading = false, platformInfo = PlatformInfo(name = "Android", version = "38")),
                expectMostRecentItem(),
            )
        }
    }
}
