# {{PROJECT_NAME}}

App Kotlin Multiplatform para Android e iOS, con la UI compartida en Compose Multiplatform.

## Stack

- Perfil: hb-stack-mobile-kotlin
- Variante: kmp
- Package base: `{{PACKAGE_NAME}}`
- Módulos: `{{MODULE_NAME}}` (librería KMP con la UI y la lógica compartidas), `androidApp` (punto de entrada de Android) e `iosApp` (proyecto Xcode, punto de entrada de iOS).
- DI: Koin.
- Versiones: **siempre** en `gradle/libs.versions.toml`; no las asumas.
- Calidad: ktlint, detekt y Android Lint (`abortOnError = true`; `androidApp` analiza también el módulo compartido con `checkDependencies`).
- iOS: Xcode 26 o superior (la versión del CI); deployment target iOS 18.0.

## Comandos

| Para | Comando |
| --- | --- |
| Compilar Android | `./gradlew :androidApp:assembleDebug` |
| Compilar iOS sin firma (como el CI) | `xcodebuild build -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO` |
| Tests compartidos en el host Android (JVM) | `./gradlew :shared:testAndroidHostTest` |
| Tests compartidos en el simulador de iOS | `./gradlew :shared:iosSimulatorArm64Test` |
| Formatear y analizar (ktlint + detekt + Lint) | `./gradlew formatAndAnalyze` |
| Solo verificar la calidad, sin formatear (CI) | `./gradlew checkCodeQuality` |
| Hook de calidad (una vez, tras clonar) | `./scripts/setup-quality-hook.sh` |

## DoD

Antes de dar una tarea por terminada, en este orden:

1. `./gradlew formatAndAnalyze`
2. `./gradlew :shared:testAndroidHostTest`
3. `./gradlew :shared:iosSimulatorArm64Test`
4. `./gradlew :androidApp:assembleDebug`
5. `xcodebuild build -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO`

Los pasos 3 y 5 necesitan macOS con Xcode. Si no están disponibles, dilo en la entrega en vez de darlos por pasados.

## Arquitectura

Sigue el perfil (variante kmp). Particularidades de este proyecto:

- Todo el código vive en `{{MODULE_NAME}}`. En `commonMain`: `core/designsystem` (tema y tokens), `feature/<nombre>/{domain,data,ui,di}` y `navigation/AppNavDisplay.kt`. `androidMain` e `iosMain` repiten los paquetes de la feature con sus implementaciones de plataforma. `androidApp` (`MainActivity`) e `iosApp` (`iOSApp.swift`, `ContentView.swift`) solo arrancan la app: no les agregues lógica.
- El código de plataforma de una feature vive en la propia feature: la interfaz en `commonMain` y una implementación por plataforma, registrada en `internal expect fun <feature>PlatformModule(): Module`, que el módulo de la feature incluye. Cuando dos o más features lo necesiten, se sube a `core` con un `platformModule()`.
- Koin se inicia una sola vez, en `App.kt`, con el módulo de cada feature.
- **Cada `NavKey` nueva se registra en `navConfig` (`navigation/AppNavDisplay.kt`):** el back stack se serializa con ese módulo (iOS no tiene reflexión) y una clave sin registrar hace fallar la app al guardar el estado.
- `MainViewController()`, en `iosMain`, es el puente con SwiftUI. Va en PascalCase porque Swift lo llama como `MainViewControllerKt.MainViewController()`.
- Tests: `commonTest` para la lógica compartida, con `runTestWithMainDispatcher` (`core/testing`) en los de ViewModel; `androidHostTest` e `iosTest` para lo propio de cada plataforma. Cada módulo de Koin tiene su test con `verify()` de koin-test en `androidHostTest` (solo existe en JVM).
- `feature/home` es el ejemplo del template: `init-project.sh` lo reduce a una pantalla mínima (`HomeNavKey`, `homeEntry()` y `HomeScreen`).

## Reglas del proyecto

- Imports con comodín (`import x.*`) prohibidos: ktlint los rechaza en el build (`.editorconfig`).
- **El deployment target de iOS está en tres lugares que deben coincidir:** `iosApp/Configuration/Config.xcconfig`, `iosApp/iosApp.xcodeproj/project.pbxproj` y el `osVersionMin` de `{{MODULE_NAME}}/build.gradle.kts`. El framework de Kotlin/Native se enlaza con su propio mínimo; si no coincide con el de Xcode, el link avisa o falla.

## Git

- Commits: Conventional Commits, en español, sin emoji y sin atribución de IA.
- Rama principal: `main`. Se trabaja en ramas cortas y se integra por PR con squash.
- **La IA no commitea:** deja los cambios sin commitear y entrega el mensaje. Tampoco hace `push`, `tag` ni `merge`.

## Versionado y release

- SemVer. La versión vive en dos lugares que se suben juntos: `androidApp/build.gradle.kts` (`versionName`; `versionCode` sube de 1 en 1) e `iosApp/Configuration/Config.xcconfig` (`MARKETING_VERSION`; `CURRENT_PROJECT_VERSION` sube de 1 en 1).
- Tags `vX.Y.Z`, creados por la persona. Al subir el tag, el workflow `release.yml` compila el APK y el AAB de Android y crea el GitHub Release: no lo crees a mano. iOS no se publica desde el CI.
- `CHANGELOG.md` con Keep a Changelog, en español: cada cambio entra en `[Unreleased]`.

## Seguridad

- Sigue el perfil (OWASP MASVS). Ningún secreto en el repo: van en `local.properties` o en los secretos de CI (la firma de release usa los que describe `release.yml`).
