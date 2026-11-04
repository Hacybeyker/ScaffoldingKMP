# Changelog — ScaffoldingKMP

> Todos los cambios notables de este proyecto están documentados aquí.  
> Formato basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/).  
> El template se versiona solo en este archivo: no se crean tags ni GitHub Releases.

---

## [Unreleased]

### Added
- Hook de pre-commit de calidad (`scripts/setup-quality-hook.sh`): corre `./gradlew formatAndAnalyze` antes de cada commit. `init-project.sh` lo instala; quien clone el proyecto lo instala con `./scripts/setup-quality-hook.sh`.
- `init-project.sh` pide el nombre visible de la app (`PRODUCT_DISPLAY_NAME` en `Config.xcconfig` e `Info.plist` para iOS), renombra la carpeta raíz del proyecto y limpia los metadatos del IDE.
- `init-project.sh --github-user` (también lo pregunta en modo interactivo y acepta la variable `GITHUB_USER`): el usuario u organización de GitHub del proyecto nuevo alimenta los badges del README y el link del CHANGELOG. Sin valor queda el placeholder `TU_USUARIO`, y el script lo avisa al terminar.

### Changed
- iOS: deployment target mínimo 18.0 (antes 16.0) y Xcode 26 o superior, la versión con la que compila el CI (el job de iOS corre en `macos-26`).
- El template se versiona solo en `CHANGELOG.md`, sin tags ni GitHub Releases. La regla está en `AGENTS.md` y `init-project.sh` la quita en los proyectos nuevos, que sí publican versiones con tags.
- **Dependencias al día:** Gradle 9.7.1 → 9.8.2 (wrapper regenerado con `./gradlew wrapper`, con `distributionSha256Sum`), AGP 9.2.1 → 9.5.0, Kotlin 2.4.20 → 2.4.21, Compose Multiplatform 1.12.0 → 1.12.2, AppCompat 1.7.1 → 1.8.0 y Core 1.19.0 → 1.19.1. AGP y Gradle superan el rango que Kotlin 2.4.21 publica como probado (AGP hasta 9.3.1, Gradle hasta 9.7.0): Kotlin admite versiones más nuevas y el CI las verifica en Android e iOS.
- Las tareas globales de calidad se registran con `tasks.register("…")`: la sintaxis `by tasks.registering` está deprecada desde Gradle 9.6.
- **Infraestructura de IA con skills globales:** el proyecto solo aporta `AGENTS.md` (estándar [agents.md](https://agents.md/)) y `CLAUDE.md`, que lo importa. Las skills [hb-*](https://github.com/Hacybeyker/agent-skills) se instalan una vez por máquina y la arquitectura sigue el perfil `hb-stack-mobile-kotlin` (variante `kmp`). `AGENTS.md` pasa a la estructura estándar (Stack, Comandos, DoD, Arquitectura, Reglas del proyecto, Git, Versionado y release, Seguridad), con un DoD que cubre Android e iOS. `init-project.sh` solo completa los placeholders de `AGENTS.md` y avisa si las skills `hb-*` no están instaladas.
- **Código alineado con el perfil `hb-stack-mobile-kotlin` (variante `kmp`), con el mismo esqueleto que el template Android:** la feature de ejemplo pasa a llamarse `home` (`domain`/`data`/`ui`/`di`) y sigue mostrando la plataforma. La pantalla expone un único `uiState` y un único `onAction(HomeAction)`, con la acción `Refresh`. La plataforma llega por DI: `PlatformInfoSource` en `commonMain`, con una implementación por plataforma en `homePlatformModule()`. Se agregan el design system (`core/designsystem`), Navigation 3 de Compose Multiplatform (`AppNavDisplay`, con cada `NavKey` registrada para iOS), los textos en Compose Resources y los `@PreviewLightDark` en `commonMain`. Koin arranca con la API no deprecada (`KoinApplication(configuration = koinConfiguration { … })`).
- Tests con fakes en cada capa, tests del `PlatformInfoSource` de Android y de iOS, `verify()` del grafo de Koin y `runTestWithMainDispatcher` (`core/testing`) para fijar `Dispatchers.Main` en `commonTest`. Dependencias nuevas: Navigation 3 1.2.0, `lifecycle-viewmodel-navigation3` 2.11.0, el plugin de kotlinx.serialization y `koin-test` 4.2.2.
- `init-project.sh` reduce la feature `home` a una pantalla mínima en todos los source sets y reordena los imports según `.editorconfig` tras el cambio de package.
- **CI y release:** los runners Linux pasan de `ubuntu-latest` a `ubuntu-26.04` fijo, cada action queda fijada por SHA (incluida `maxim-lobanov/setup-xcode`, que es de un tercero; Dependabot las mantiene al día) y el CI publica un Build Scan por job solo si el repo es público: los scans van a la instancia pública de Gradle y no heredan la privacidad del repo.

### Removed
- `--module` de `init-project.sh` y su pregunta: no renombraba el módulo, solo cambiaba el texto de `AGENTS.md`, que quedaba describiendo un módulo inexistente. El módulo compartido siempre es `shared`, la estructura por defecto de JetBrains; `--module shared` se sigue aceptando para no romper scripts.
- `htmlReport` y `xmlReport` de Lint: están deprecadas y AGP ya genera siempre los reportes.
- Workaround `disableNativeCache` de Kotlin/Native para Xcode 16.4: con Kotlin 2.4.21 y Xcode 26 o superior ya no hace falta.
- La carpeta `.agents` (skills copiadas al repo y sus scripts de sincronización) y los symlinks por editor que creaba `init-project.sh` (`.claude/skills`, `.cursor`, `.github/copilot`, `.jetbrains`, `.junie`, `.antigravity` y `.agent`), junto con sus entradas en `.gitignore`.
- La feature de ejemplo `platforminfo`, con su contrato State/Event/Effect por `Channel`. También `Platform.kt` con sus `actual` de Android e iOS, `Greeting.kt` y `GreetingUtil.kt` (restos del asistente de proyectos, sin uso), el preview de `androidMain`, `AppAndroidPreview` de `MainActivity` (arrancaba Koin dentro de un preview) y los tests de relleno `assertEquals(3, 1 + 2)`.

### Fixed
- Un nombre visible con `&`, `|`, `\`, comillas o un `@`/`?` al inicio rompía el reemplazo de `init-project.sh` o el `strings.xml` de Android: ahora se escapa para sed, XML y aapt2.
- El CHANGELOG que genera `init-project.sh` enlazaba a `TU_ORG` aunque se pasara `GITHUB_USER`, y usaba encabezados con emoji: ahora usa el usuario indicado y los encabezados estándar de Keep a Changelog.
- Los badges de versión del README generado estaban fijos y viejos (Kotlin 2.4.0, Compose Multiplatform 1.11.1, iOS 16+): ahora se leen del catálogo y de `Config.xcconfig`.
- `init-project.sh --help` mostraba la ayuda cortada.
- El pre-commit hook que instala `scripts/setup-quality-hook.sh` re-stageaba todo archivo modificado y metía en el commit cambios que se habían dejado fuera a propósito. Ahora solo re-stagea los archivos Kotlin que ya formaban parte del commit. Si ya lo tienes instalado, vuelve a correr `./scripts/setup-quality-hook.sh`.

---

## [1.0.0] — 2026-06-12

Primera versión pública del scaffolding. Incluye una base KMP production-ready con
infraestructura de IA, calidad de código, CI/CD y un ejemplo completo de feature.

### Added

#### Proyecto base KMP

- **Kotlin Multiplatform** con targets Android e iOS (`shared`, `androidApp`, `iosApp`)
- **Compose Multiplatform 1.11.1** como UI compartida con Material Design 3
- **`gradle/libs.versions.toml`** como catálogo centralizado de versiones (Kotlin 2.4.0, AGP 9.2.1, Compose 1.11.1, Koin 4.2.1)
- **`settings.gradle.kts`** y `build.gradle.kts` raíz con estructura multi-módulo
- Íconos de lanzador Android y assets de iOS (`AppIcon`, `AccentColor`)
- `expect`/`actual` de `Platform` para obtener el nombre de plataforma en iOS y Android

#### Infraestructura de IA (`AGENTS.md` + `.agents/`)

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

#### `init-project.sh`

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

#### Calidad de código

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

#### CI/CD (GitHub Actions)

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

#### Actualizaciones de versiones

- AGP `9.0.1 → 9.2.1` (requiere Gradle ≥ 9.4.1)
- `android-compileSdk` y `android-targetSdk` `36 → 37`
- Gradle wrapper `9.4.1 → 9.5.1` con SHA-256 verificado
- Corregido: bloque `androidLibrary {}` deprecado → `android {}` en `shared/build.gradle.kts`

#### Feature de ejemplo: `PlatformInfo`

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

#### Skill `feature-implementation`

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

#### Skill `feature-implementation` (mejoras al template)

- `report-template.html`: nuevo bloque `.change-summary` por archivo con descripción de impacto:
  - **Archivo nuevo** — columna única "✅ Por qué se crea"
  - **Archivo modificado** — columna "⚠️ Antes" (limitación) + "✅ Después" (beneficio)
  - **Archivo eliminado** — columna única "🗑️ Por qué se elimina"
  - Guía de uso al pie del template actualizada con los tres patrones de HTML

#### Calidad de código (Hard Rules)

- `kmp-best-practices/SKILL.md`: nueva sección `⛔ Hard Rules` con reglas no negociables:
  - Solo versiones estables en `libs.versions.toml` (prohibido alpha/beta/rc/snapshot)
  - `libs.versions.toml` como única fuente de versiones; siempre `version.ref`, nunca literal en `.kts`
  - Sin comentarios inline en el catálogo de versiones

### Fixed

#### Código

- `App.kt` en `commonMain`: wildcard import `androidx.compose.runtime.*` expandido a imports explícitos (regla `no-wildcard-imports` de ktlint)
- `PlatformInfoContent.kt`: `@Preview` movido de `commonMain` a `androidMain` para evitar `Unresolved reference 'ui'` en `org.jetbrains.compose.ui.tooling.preview`
- `KoinApplication` en `App.kt`: agregado `@Suppress("DEPRECATION")` ya que el nuevo API `KoinApplication(config:)` aún no está disponible en Koin 4.2.1
- `PlatformInfoViewModelTest.kt`: aserciones de safe call `?.` mejoradas a `assertNotNull(x).field`
- `report-template.html`: eliminado `</span>` huérfano en la sección de archivos eliminados

#### Versiones y configuración

- `androidApp/build.gradle.kts`: `versionName` corregido de `"1.0"` a `"1.0.0"` (semver)
- `iosApp/Configuration/Config.xcconfig`: `MARKETING_VERSION` corregido de `1.0` a `1.0.0`; añadido `IPHONEOS_DEPLOYMENT_TARGET=16.0`
- `gradle/libs.versions.toml`: `androidx-lifecycle` `2.11.0-beta01` → `2.10.0` (estable); `material3` `1.11.0-alpha07` → `1.9.0` (estable); eliminados comentarios inline
- `iosApp/iosApp.xcodeproj/project.pbxproj`: `IPHONEOS_DEPLOYMENT_TARGET = 18.2` → `16.0` (mínimo real de CMP 1.11.x)
- `shared/build.gradle.kts`: añadido `-Xoverride-konan-properties=osVersionMin=16.00` para targets iOS — alinea el deployment target del linker de Kotlin/Native con el del xcodeproj
- `.github/workflows/ci.yml` (job `ios`): añadido step `Get Xcode version`; clave de caché de Konan incluye ahora la versión de Xcode para invalidar automáticamente al actualizar el runner — corrige fallo de link por klib caches obsoletas (`UIViewLayoutRegion` undefined en iOS < 16)

---

[Unreleased]: https://github.com/Hacybeyker/ScaffoldingKMP/commits/main
