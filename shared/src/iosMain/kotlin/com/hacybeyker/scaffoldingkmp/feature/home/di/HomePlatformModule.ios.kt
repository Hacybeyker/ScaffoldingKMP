package com.hacybeyker.scaffoldingkmp.feature.home.di

import com.hacybeyker.scaffoldingkmp.feature.home.data.source.IosPlatformInfoSource
import com.hacybeyker.scaffoldingkmp.feature.home.data.source.PlatformInfoSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun homePlatformModule(): Module = module {
    singleOf(::IosPlatformInfoSource) bind PlatformInfoSource::class
}
