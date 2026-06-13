package com.hacybeyker.scaffoldingkmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.di.platformInfoModule
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.PlatformInfoScreen
import org.koin.compose.KoinApplication

@Suppress("DEPRECATION")
@Composable
fun App() {
    KoinApplication(application = { modules(platformInfoModule) }) {
        MaterialTheme {
            PlatformInfoScreen()
        }
    }
}
