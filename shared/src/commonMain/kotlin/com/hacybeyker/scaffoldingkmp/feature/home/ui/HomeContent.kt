package com.hacybeyker.scaffoldingkmp.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.hacybeyker.scaffoldingkmp.core.designsystem.ScaffoldingKMPTheme
import com.hacybeyker.scaffoldingkmp.core.designsystem.spacing
import com.hacybeyker.scaffoldingkmp.feature.home.domain.model.PlatformInfo
import org.jetbrains.compose.resources.stringResource
import scaffoldingkmp.shared.generated.resources.Res
import scaffoldingkmp.shared.generated.resources.home_greeting
import scaffoldingkmp.shared.generated.resources.home_platform
import scaffoldingkmp.shared.generated.resources.home_refresh

@Composable
fun HomeContent(uiState: HomeUiState, onAction: (HomeAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.screen),
            contentAlignment = Alignment.Center,
        ) {
            val platformInfo = uiState.platformInfo
            if (platformInfo == null) {
                CircularProgressIndicator()
            } else {
                PlatformInfoDetails(
                    platformInfo = platformInfo,
                    isRefreshing = uiState.isLoading,
                    onRefresh = { onAction(HomeAction.Refresh) },
                )
            }
        }
    }
}

@Composable
private fun PlatformInfoDetails(
    platformInfo: PlatformInfo,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val platform = stringResource(Res.string.home_platform, platformInfo.name, platformInfo.version)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = platform, style = MaterialTheme.typography.headlineMedium)
        Text(text = stringResource(Res.string.home_greeting, platform), style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = onRefresh,
            enabled = !isRefreshing,
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
        ) {
            Text(text = stringResource(Res.string.home_refresh))
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    ScaffoldingKMPTheme {
        HomeContent(
            uiState = HomeUiState(isLoading = false, platformInfo = PlatformInfo(name = "Android", version = "37")),
            onAction = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeContentLoadingPreview() {
    ScaffoldingKMPTheme {
        HomeContent(uiState = HomeUiState(), onAction = {})
    }
}
