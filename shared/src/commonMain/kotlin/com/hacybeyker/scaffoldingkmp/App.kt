package com.hacybeyker.scaffoldingkmp

import androidx.compose.runtime.Composable
import com.hacybeyker.scaffoldingkmp.core.designsystem.ScaffoldingKMPTheme
import com.hacybeyker.scaffoldingkmp.feature.home.di.homeModule
import com.hacybeyker.scaffoldingkmp.navigation.AppNavDisplay
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(homeModule) }) {
        ScaffoldingKMPTheme {
            AppNavDisplay()
        }
    }
}
