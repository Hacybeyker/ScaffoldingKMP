package com.hacybeyker.scaffoldingkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
