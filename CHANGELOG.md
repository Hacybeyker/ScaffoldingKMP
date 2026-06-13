# Changelog — ScaffoldingKMP

> Todos los cambios notables de este proyecto están documentados aquí.  
> Formato basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/).

---

## [1.0.0] — 2026-06-12

Primera versión pública del scaffolding. Incluye una base KMP production-ready con
infraestructura de IA, calidad de código, CI/CD y un ejemplo completo de feature.

### ✨ Added — Proyecto base KMP

- **Kotlin Multiplatform** con targets Android e iOS (`shared`, `androidApp`, `iosApp`)
- **Compose Multiplatform 1.11.1** como UI compartida con Material Design 3
- **`gradle/libs.versions.toml`** como catálogo centralizado de versiones (Kotlin 2.4.0, AGP 9.2.1, Compose 1.11.1, Koin 4.2.1)
- **`settings.gradle.kts`** y `build.gradle.kts` raíz con estructura multi-módulo
- Íconos de lanzador Android y assets de iOS (`AppIcon`, `AccentColor`)
- `expect`/`actual` de `Platform` para obtener el nombre de plataforma en iOS y Android

### ✨ Added — Infraestructura de IA (`AGENTS.md` + `.agents/`)

- **`AGENTS.md`** como fuente de verdad para agentes de IA (estándar [agents.md](https://agents.md/))
  - Tech stack, estructura de módulos, reglas de arquitectura, hard rules
  - Principios SOLID, MVI-lite (State/Event/Effect), separación Screen/Content
- **`.agents/skills/kmp-best-practices/`** — skill maestra KMP con 5 referencias:
  - `ARCHITECTURE_AND_WORKFLOW.md` — Flujo de 6 pasos (domain → data → DI → presentation)
  - `UI_AND_STYLING_GUIDE.md` — Compose patterns y Material Design 3
  - `TESTING_STRATEGIES.md` — Tests unitarios con Fakes, Turbine y Roborazzi
  - `PLATFORM_IMPLEMENTATIONS.md` — Room KMP, Ktor y `expect`/`actual`
  - `MOBILE_SECURITY_GUIDE.md` — SSL Pinning, cifrado, secretos y hardening
- **`.agents/skills/git-commit/`** — skill de commits convencionales con íconos
- **`.agents/skills/changelog-generator/`** — skill para generar changelogs
- **`.agents/skills/skill-creator/`** — skill para crear nuevas skills con scripts `init_skill.py` y `package_skill.py`
- **`.agents/skills/skill-linker/`** — skill para vincular skills entre proyectos
- **`.agents/scripts/sync-skills.sh`** — sincroniza skills como symlinks en 7 IDEs (Claude Code, Copilot, Cursor, JetBrains, Junie, Antigravity, Agent)
- **`.agents/scripts/remove-skill.sh`** — elimina una skill y sus symlinks

### ✨ Added — `init-project.sh`

- Script interactivo y no interactivo para convertir el scaffolding en un proyecto real
- Flags: `--name`, `--package`, `--app-name`, `--module`, `--yes`, `--help`
- Validaciones de PascalCase (nombre) y formato de package
- Guarda contra doble ejecución (detecta si el package ya fue renombrado)
- Reemplaza package, nombre de proyecto e imports de Compose Resources en todos los archivos
- Mueve directorios de código fuente al nuevo path de package
- **Paso 4.5** — limpia el código de ejemplo del scaffolding:
  - Elimina `feature/platforminfo/` de todos los source sets
  - Elimina `Greeting.kt` y `GreetingUtil.kt` (archivos de plantilla KMP)
  - Restaura `App.kt` limpio con solo `MaterialTheme { // TODO }`
- Reemplaza placeholders (`{{PROJECT_NAME}}`, `{{PACKAGE_NAME}}`, etc.) en `AGENTS.md` y `.agents/`
- Ejecuta `sync-skills.sh` automáticamente
- Genera nuevo `README.md` para el proyecto destino
- Genera nuevo `CHANGELOG.md` limpio para el proyecto destino
- Elimina `SETUP.md` e `init-project.sh` (opcional)
- Reinicia historial de git con commit inicial (opcional)
- **`SETUP.md`** — guía paso a paso de cómo usar el inicializador

### ✨ Added — Calidad de código

- **ktlint 14.2.0** (engine 1.8.0) configurado únicamente vía `.editorconfig`:
  - `ktlint_code_style = android_studio`
  - `ktlint_function_naming_ignore_when_annotated_with = Composable, Preview`
  - `ktlint_standard_filename = disabled` (compatible con `expect`/`actual`)
  - Sin wildcard imports (`ij_kotlin_name_count_to_use_star_import = 999`)
  - Trailing commas habilitadas, `max_line_length = 120`
- **detekt 1.23.8** con `config/detekt/detekt.yml` y `buildUponDefaultConfig = true`:
  - `MatchingDeclarationName: active: false` (convención KMP `expect`/`actual`)
  - `UndocumentedPublicClass/Function/Property: active: false`
  - `FunctionNaming: ignoreAnnotated: ['Composable']`
  - `LongParameterList: ignoreAnnotated: ['Composable']`
  - `UnusedPrivateMember: ignoreAnnotated: ['Preview']`
  - Test folders ampliados: incluye `androidHostTest` e `iosTest`
  - `maxIssues: 0` — cualquier hallazgo falla el build
- **Android Lint** con `checkDependencies = true` en `androidApp` (analiza módulo `shared` también)
- **Tareas Gradle agregadas** en `build.gradle.kts` raíz:
  - `./gradlew formatAndAnalyze` — ktlint format + detekt + lint (pre-commit)
  - `./gradlew checkCodeQuality` — solo verificación (CI)
  - `./gradlew ktlintFormatAll` / `ktlintCheckAll` / `detektAll` / `lintAll`
- `@Suppress("ktlint:standard:function-naming", "FunctionNaming")` en `MainViewController.kt` (PascalCase requerido por Swift interop)

### ✨ Added — CI/CD (GitHub Actions)

- **`.github/workflows/ci.yml`** — Pipeline en 3 jobs con dependencias secuenciales:
  1. `quality` — ktlint + detekt + Android Lint (ubuntu, 15 min timeout)
  2. `android` — tests + `assembleDebug` (ubuntu, 30 min) — depende de `quality`
  3. `ios` — `iosSimulatorArm64Test` + `xcodebuild` (macos-latest, 45 min) — depende de `quality`
  - Cancelación automática de runs previos (`concurrency: cancel-in-progress: true`)
  - Cache de `~/.konan` para compilaciones iOS
  - Triggers: push/PR a `main` y `develop`
- **`.github/workflows/release.yml`** — Release automático en tags `v*`:
  - Calidad → tests → `assembleRelease` + `bundleRelease` → `gh release create --generate-notes`
  - Comentarios guía para agregar firma con secrets
- **`.github/dependabot.yml`** — Actualizaciones semanales agrupadas (Gradle + GitHub Actions)
- **`iosApp.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme`** — Scheme compartido en git para que `xcodebuild -scheme iosApp` funcione en CI sin `xcuserdata`

### ✨ Added — Actualizaciones de versiones

- AGP `9.0.1 → 9.2.1` (requiere Gradle ≥ 9.4.1)
- `android-compileSdk` y `android-targetSdk` `36 → 37`
- Gradle wrapper `9.4.1 → 9.5.1` con SHA-256 verificado
- Corregido: bloque `androidLibrary {}` deprecado → `android {}` en `shared/build.gradle.kts`

### ✨ Added — Feature de ejemplo: `PlatformInfo`

Feature completa que demuestra **todas las capas de Clean Architecture** con MVI-lite y Koin.
Se auto-elimina cuando se ejecuta `init-project.sh`:

**Domain:**
- `PlatformInfo.kt` — entidad inmutable
- `PlatformInfoRepository.kt` — interfaz del repositorio
- `GetPlatformInfoUseCase.kt` — use case con `operator fun invoke()`

**Data:**
- `PlatformInfoDto.kt` — DTO interno (`internal data class`)
- `PlatformInfoMapper.kt` — mapper DTO → Domain (`extension fun toDomain()`)
- `PlatformInfoRepositoryImpl.kt` — implementación interna usando `Platform`

**DI:**
- `PlatformInfoModule.kt` — módulo Koin con `viewModelOf`, `factory`, `single`

**Presentation:**
- `PlatformInfoState/Event/Effect.kt` — MVI-lite: state inmutable, events, one-shot effects
- `PlatformInfoViewModel.kt` — `StateFlow` + `Channel<Effect>`
- `PlatformInfoScreen.kt` — composable stateful (`koinViewModel()`)
- `PlatformInfoContent.kt` — composable stateless (commonMain, sin `@Preview`)
- `PlatformInfoContentPreview.kt` — `@Preview` en `androidMain` (import `androidx.*` correcto)

**Tests (`commonTest`):**
- `PlatformInfoMapperTest.kt` — verifica mapeo DTO → Domain
- `GetPlatformInfoUseCaseTest.kt` — verifica delegación al repositorio con Fake
- `PlatformInfoViewModelTest.kt` — verifica estados con Turbine + `runTest`

**Dependencias de test añadidas:**
- `kotlinx-coroutines-test 1.11.0`
- `app.cash.turbine:turbine 1.2.1`

### ✨ Added — Skill `feature-implementation`

- **`.agents/skills/feature-implementation/SKILL.md`** — workflow de 5 fases:
  1. Context Loading (AGENTS.md + referencias según el tipo de tarea)
  2. Snapshot (captura estado "antes" de archivos a modificar)
  3. Implementation (Feature / BugFix / Refactor con guía paso a paso)
  4. Definition of Done (ktlint + detekt + lint + tests + compile Android/iOS)
  5. HTML Report + handoff sin commit automático
- **`.agents/skills/feature-implementation/assets/report-template.html`** — template HTML profesional con:
  - Topbar sticky, badges por tipo de tarea, cards de estadísticas
  - Secciones colapsables por archivo (Nuevo / Modificado / Eliminado)
  - Tabs Antes / Después / Diff con syntax highlight
  - Tabla de DoD con pills ✅ PASS / ❌ FAIL / ⏭️ SKIPPED
  - Guía de uso embebida en comentarios HTML
- `reports/` agregado a `.gitignore`

### 🔧 Fixed

- `App.kt` en `commonMain`: wildcard import `androidx.compose.runtime.*` expandido a imports explícitos (regla `no-wildcard-imports` de ktlint)
- `PlatformInfoContent.kt`: `@Preview` movido de `commonMain` a `androidMain` para evitar error de compilación Android (`Unresolved reference 'ui'` en `org.jetbrains.compose.ui.tooling.preview`)
- `KoinApplication` en `App.kt`: agregado `@Suppress("DEPRECATION")` ya que el nuevo API `KoinApplication(config:)` aún no está disponible en Koin 4.2.1
- `PlatformInfoViewModelTest.kt`: aserciones de safe call `?.` mejoradas a `assertNotNull(x).field` (aprovecha el contrato de retorno de `assertNotNull`)

---

[1.0.0]: https://github.com/hacybeyker/ScaffoldingKMP/releases/tag/v1.0.0
