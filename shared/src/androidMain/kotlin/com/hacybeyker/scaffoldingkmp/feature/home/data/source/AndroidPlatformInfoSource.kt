package com.hacybeyker.scaffoldingkmp.feature.home.data.source

import android.os.Build
import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto

internal class AndroidPlatformInfoSource : PlatformInfoSource {
    override fun getPlatformInfo(): PlatformInfoDto =
        PlatformInfoDto(systemName = "Android", systemVersion = Build.VERSION.SDK_INT.toString())
}
