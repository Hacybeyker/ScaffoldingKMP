package com.hacybeyker.scaffoldingkmp.feature.home.data.source

import com.hacybeyker.scaffoldingkmp.feature.home.data.model.PlatformInfoDto

/** Lee los datos del sistema. Cada plataforma tiene su implementación en androidMain e iosMain. */
internal interface PlatformInfoSource {
    fun getPlatformInfo(): PlatformInfoDto
}
