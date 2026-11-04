package com.hacybeyker.scaffoldingkmp.feature.home.di

import com.hacybeyker.scaffoldingkmp.feature.home.data.repository.DevicePlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.home.domain.repository.PlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.home.domain.usecase.GetPlatformInfoUseCase
import com.hacybeyker.scaffoldingkmp.feature.home.ui.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    includes(homePlatformModule())
    singleOf(::DevicePlatformInfoRepository) bind PlatformInfoRepository::class
    factoryOf(::GetPlatformInfoUseCase)
    viewModelOf(::HomeViewModel)
}

/** Las implementaciones de plataforma de la feature (androidMain e iosMain). */
internal expect fun homePlatformModule(): Module
