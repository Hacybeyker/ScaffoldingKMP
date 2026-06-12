# 📖 SETUP — De Scaffolding a tu nuevo proyecto KMP

Esta guía explica, paso a paso, cómo convertir **ScaffoldingKMP** en tu propio proyecto Kotlin Multiplatform.

---

## 1. Requisitos previos

| Herramienta | Versión | Notas |
|-------------|---------|-------|
| JDK | 21 | Gradle lo descarga solo vía toolchain (`gradle/gradle-daemon-jvm.properties`) |
| Android Studio | Reciente (con plugin KMP) | Para Android y código compartido |
| Xcode | Reciente | Solo para la app iOS (requiere macOS) |
| Git | Cualquiera | Para clonar y versionar |

## 2. Obtener la plantilla

**Opción A — Clonar con el nombre final del proyecto (recomendado):**

```bash
git clone https://github.com/hacybeyker/ScaffoldingKMP.git MiAppGenial
cd MiAppGenial
```

**Opción B — Usar como GitHub Template:** pulsa **"Use this template"** en GitHub, crea tu repo y clónalo.

## 3. Ejecutar el inicializador

### Modo interactivo

```bash
./init-project.sh
```

El script pregunta:

| Pregunta | Ejemplo | Regla |
|----------|---------|-------|
| 📌 Nombre del Proyecto | `MiAppGenial` | PascalCase, sin espacios ni guiones |
| 📦 Package base | `com.empresa.miapp` | Minúsculas separadas por puntos |
| 🏷️ Nombre visible de la app | `Mi App Genial` | Libre (puede tener espacios). Default: el nombre del proyecto |
| 🧩 Módulo principal de Compose | `shared` | Déjalo por defecto salvo que reestructures los módulos |

Al final ofrece dos limpiezas opcionales (recomendadas):

- 🧹 **Eliminar archivos del scaffolding** (`SETUP.md` y el propio `init-project.sh`).
- 🌱 **Reiniciar el historial de git** (borra el historial de la plantilla y crea un commit inicial limpio).

### Modo no interactivo (CI / agentes de IA)

```bash
./init-project.sh \
  --name MiAppGenial \
  --package com.empresa.miapp \
  --app-name "Mi App Genial" \
  --yes
```

Con `--yes` no se hacen preguntas: se confirma todo, se eliminan los archivos del scaffolding y se reinicia git.

## 4. ¿Qué modifica exactamente el script?

| Área | Archivos | Cambio |
|------|----------|--------|
| **Gradle** | `settings.gradle.kts` | `rootProject.name` |
| **Android** | `androidApp/build.gradle.kts`, `shared/build.gradle.kts` | `namespace` y `applicationId` |
| **Android** | `androidApp/src/main/res/values/strings.xml` | `app_name` (nombre visible) |
| **Kotlin** | `shared/src/**` y `androidApp/src/**` | Declaraciones `package`/`import` y **carpetas movidas** al nuevo package |
| **Compose Resources** | `shared/src/commonMain/**` | Imports de la clase generada `Res` (`scaffoldingkmp.shared.generated.resources` → `<tunombre>.shared.generated.resources`) |
| **iOS** | `iosApp/Configuration/Config.xcconfig` | `PRODUCT_NAME` y `PRODUCT_BUNDLE_IDENTIFIER` |
| **iOS** | `iosApp/iosApp.xcodeproj/project.pbxproj` | Referencia al `.app` generado |
| **Docs IA** | `AGENTS.md`, `.agents/**` | Placeholders `{{PROJECT_NAME}}`, `{{PACKAGE_NAME}}`, `{{MODULE_NAME}}`, `{{PACKAGE_PATH}}`, `{{PROJECT_ROOT}}` |
| **IDE/IA** | `.claude/`, `.cursor/`, `.github/copilot/`, `.jetbrains/`, `.junie/`, `.antigravity/`, `.agent/` | Symlinks hacia `.agents/skills/` (vía `sync-skills.sh`) |
| **README** | `README.md` | Se regenera con la información de tu proyecto |

## 5. Verificación post-setup

```bash
# 1. No deben quedar referencias al scaffolding en el código
#    (README.md conserva el link de atribución a la plantilla; es normal)
grep -ri "scaffoldingkmp\|com.hacybeyker" --exclude-dir=.git --exclude-dir=build --exclude=README.md . || echo "✅ Limpio"

# 2. El proyecto compila
./gradlew :androidApp:assembleDebug

# 3. Los tests pasan
./gradlew :shared:testAndroidHostTest
```

Para iOS:

1. Abre `iosApp/` en Xcode.
2. Configura tu `TEAM_ID` en `iosApp/Configuration/Config.xcconfig` (necesario para firmar).
3. Ejecuta en un simulador.

## 6. Empezar a desarrollar con IA

Abre tu agente de IA (Claude Code, Copilot, Cursor, Junie…) en la raíz del proyecto y dile:

> *"Lee AGENTS.md y ayúdame a implementar mi primera feature."*

El agente seguirá automáticamente:

- **Arquitectura**: Clean Architecture + MVVM + State/Event/Effect (`.agents/skills/kmp-best-practices/`).
- **Commits**: mensajes semánticos con `/commit` (`.agents/skills/git-commit/`).
- **Changelog**: notas de versión con `/changelog`.
- **Reglas duras**: sin lógica en Composables, sin DTOs fuera de data, sin secretos hardcodeados.

## 7. Pasos manuales opcionales

- **Renombrar la carpeta raíz** si no clonaste con el nombre final: `cd .. && mv ScaffoldingKMP MiAppGenial`.
- **Icono de la app**: reemplaza los `mipmap` en `androidApp/src/main/res/` y `Assets.xcassets` en `iosApp/`.
- **Dependencias**: agrega Koin, Ktor, Room, etc. en `gradle/libs.versions.toml` (la IA sabe usarlo — pídeselo).
- **Repo nuevo en GitHub**: `git remote add origin <url> && git push -u origin main`.

## 8. Solución de problemas

| Problema | Solución |
|----------|----------|
| `Permission denied` al ejecutar el script | `chmod +x init-project.sh` |
| "Este scaffolding ya fue inicializado" | El script solo puede ejecutarse una vez; clona la plantilla de nuevo |
| Gradle no sincroniza tras renombrar | En Android Studio: **File → Invalidate Caches / Restart** |
| Xcode no encuentra el framework `Shared` | Compila primero desde Xcode (el build phase ejecuta `embedAndSignAppleFrameworkForXcode`) |
| Symlinks rotos en Windows | Ejecuta el script desde **Git Bash** con permisos de symlink habilitados (Developer Mode) |
