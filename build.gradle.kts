plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

// Módulos con código Kotlin analizable. Si agregas un módulo nuevo, inclúyelo aquí
// y aplica los plugins de calidad en su build.gradle.kts (ver shared/build.gradle.kts).
val qualityModules = listOf(":shared", ":androidApp")

subprojects {
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "11"
    }
}

// ── Tareas globales de calidad ───────────────────────────────────────────────

val detektAll by tasks.registering {
    group = "verification"
    description = "Run detekt on all modules"
    dependsOn(qualityModules.map { "$it:detekt" })
}

val ktlintFormatAll by tasks.registering {
    group = "formatting"
    description = "Run ktlint format on all modules"
    dependsOn(qualityModules.map { "$it:ktlintFormat" })
}

val ktlintCheckAll by tasks.registering {
    group = "verification"
    description = "Run ktlint check on all modules"
    dependsOn(qualityModules.map { "$it:ktlintCheck" })
}

val lintAll by tasks.registering {
    group = "verification"
    description = "Run Android Lint (androidApp analiza también shared vía checkDependencies)"
    dependsOn(":androidApp:lint")
}

tasks.register("checkCodeQuality") {
    group = "verification"
    description = "Run all code quality checks (ktlint + detekt + Android Lint)"
    dependsOn(ktlintCheckAll, detektAll, lintAll)
}

tasks.register("formatAndAnalyze") {
    group = "build"
    description = "Format code and run all quality checks"
    dependsOn(ktlintFormatAll)
    finalizedBy(ktlintCheckAll, detektAll, lintAll)
}
