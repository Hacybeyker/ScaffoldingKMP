package com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.entity.PlatformInfo
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.state.PlatformInfoState

@Preview(showBackground = true)
@Composable
private fun PlatformInfoContentPreview() {
    MaterialTheme {
        PlatformInfoContent(
            state = PlatformInfoState(
                platformInfo = PlatformInfo(
                    name = "Android 35",
                    greeting = "Hello from Android 35!",
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlatformInfoContentLoadingPreview() {
    MaterialTheme {
        PlatformInfoContent(
            state = PlatformInfoState(isLoading = true),
        )
    }
}
