package com.hacybeyker.scaffoldingkmp.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Equivalente KMP de `MainDispatcherRule`: `kotlin.test` no tiene reglas de JUnit. Cambia `Dispatchers.Main`
 * por [testDispatcher] durante el test, así `viewModelScope` corre en el scheduler de [runTest].
 *
 * En JVM y Native, `runTest` bloquea hasta que termina el test, de modo que `resetMain` corre después.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runTestWithMainDispatcher(
    testDispatcher: TestDispatcher = StandardTestDispatcher(),
    testBody: suspend TestScope.() -> Unit,
): TestResult {
    Dispatchers.setMain(testDispatcher)
    try {
        return runTest(testDispatcher, testBody = testBody)
    } finally {
        Dispatchers.resetMain()
    }
}
