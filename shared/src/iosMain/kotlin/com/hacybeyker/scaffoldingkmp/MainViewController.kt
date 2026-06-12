package com.hacybeyker.scaffoldingkmp

import androidx.compose.ui.window.ComposeUIViewController

// PascalCase requerido: Swift lo consume como MainViewControllerKt.MainViewController()
@Suppress("ktlint:standard:function-naming", "FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
