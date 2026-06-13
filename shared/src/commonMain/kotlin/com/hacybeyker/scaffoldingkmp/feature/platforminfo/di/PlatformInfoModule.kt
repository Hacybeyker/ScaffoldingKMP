package com.hacybeyker.scaffoldingkmp.feature.platforminfo.di

import com.hacybeyker.scaffoldingkmp.feature.platforminfo.data.repository.PlatformInfoRepositoryImpl
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.repository.PlatformInfoRepository
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.domain.usecase.GetPlatformInfoUseCase
import com.hacybeyker.scaffoldingkmp.feature.platforminfo.presentation.PlatformInfoViewModel
import com.hacybeyker.scaffoldingkmp.getPlatform
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val platformInfoModule = module {
    single<PlatformInfoRepository> { PlatformInfoRepositoryImpl(getPlatform()) }
    factory { GetPlatformInfoUseCase(get()) }
    viewModelOf(::PlatformInfoViewModel)
}
