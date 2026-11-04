package com.hacybeyker.scaffoldingkmp.feature.home.di

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

class HomeModuleTest {

    // verify() es estable, pero su parámetro opcional `injections` usa un tipo experimental.
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `home module declares every dependency it needs`() {
        homeModule.verify()
    }
}
