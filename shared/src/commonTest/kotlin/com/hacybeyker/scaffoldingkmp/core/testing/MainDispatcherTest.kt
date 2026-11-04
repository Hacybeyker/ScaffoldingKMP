package com.hacybeyker.scaffoldingkmp.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherTest {

    @Test
    fun `main coroutines run on the test scheduler`() = runTestWithMainDispatcher {
        var executed = false

        launch(Dispatchers.Main) { executed = true }

        assertFalse(executed)
        advanceUntilIdle()
        assertTrue(executed)
    }
}
