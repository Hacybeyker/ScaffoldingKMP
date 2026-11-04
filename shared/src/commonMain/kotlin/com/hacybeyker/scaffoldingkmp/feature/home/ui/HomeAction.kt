package com.hacybeyker.scaffoldingkmp.feature.home.ui

sealed interface HomeAction {
    data object Refresh : HomeAction
}
