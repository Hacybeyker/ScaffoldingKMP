# 🚀 ScaffoldingKMP

[![CI](https://github.com/hacybeyker/ScaffoldingKMP/actions/workflows/ci.yml/badge.svg)](https://github.com/hacybeyker/ScaffoldingKMP/actions/workflows/ci.yml)

> **Plantilla (scaffolding) para crear proyectos Kotlin Multiplatform — Android + iOS — con Compose Multiplatform e infraestructura de IA lista para usar.**

Clona, ejecuta un script, y en menos de un minuto tienes un proyecto KMP con tu nombre, tu package y reglas de arquitectura listas para que cualquier agente de IA (Claude Code, GitHub Copilot, Cursor, Junie, Antigravity…) trabaje con calidad profesional desde el primer prompt.

---

## ✨ ¿Qué incluye?

| Componente | Descripción |
|------------|-------------|
| **Kotlin Multiplatform** | Targets Android + iOS con módulo `shared` |
| **Compose Multiplatform** | UI compartida con Material Design 3 |
| **`init-project.sh`** | Script que renombra proyecto, package, applicationId y bundle id en un solo paso |
| **`AGENTS.md`** | Reglas del proyecto para agentes de IA (estándar [agents.md](https://agents.md/)); `CLAUDE.md` lo importa para Claude Code |
| **Skills globales** | Las skills [hb-*](https://github.com/Hacybeyker/agent-skills) (implementar, revisar, commits, releases) se instalan una vez por máquina, no por proyecto. La arquitectura sigue el perfil `hb-stack-mobile-kotlin` (variante `kmp`) |
| **Catálogo de versiones** | `gradle/libs.versions.toml` centralizado (Kotlin 2.4, AGP 9, Compose 1.11) |
| **Calidad de código** | **ktlint + detekt + Android Lint** preconfigurados con tareas agregadas (`./gradlew formatAndAnalyze`) — reglas en `.editorconfig` y `config/detekt/detekt.yml` |
| **CI/CD (GitHub Actions)** | `ci.yml` (calidad + build/tests Android e iOS en cada push/PR) y `release.yml` (APK/AAB + GitHub Release al pushear un tag `v*`) + Dependabot. Actions **fijadas por SHA** y **Build Scan** por job en repos públicos |

## 📋 Requisitos

- **JDK 21** (Gradle lo descarga automáticamente vía toolchain si no lo tienes)
- **Android Studio** (versión reciente con soporte KMP)
- **Xcode 26 o superior** (solo para compilar/ejecutar la app iOS, requiere macOS). Es la versión con la que compila el CI; en local también se probó con Xcode 27.2

## ⚡ Quick Start

```bash
# 1. Clona la plantilla con el nombre de tu nuevo proyecto
git clone https://github.com/hacybeyker/ScaffoldingKMP.git MiAppGenial
cd MiAppGenial

# 2. Ejecuta el inicializador (modo interactivo)
./init-project.sh
```

El script te preguntará el **nombre del proyecto**, el **package base** y el **nombre visible de la app**, y hará todo el resto: renombrar archivos, mover paquetes, limpiar el ejemplo, completar `AGENTS.md`, renombrar la carpeta raíz y dejar el historial de git limpio (squash automático si vienes del scaffolding, o commit encima si vienes de GitHub Template).

¿Prefieres no responder preguntas? Modo no interactivo (ideal para agentes de IA):

```bash
./init-project.sh --name MiAppGenial --package com.empresa.miapp --app-name "Mi App Genial" --yes
```

> 📖 **Guía completa paso a paso:** [SETUP.md](./SETUP.md)

## 🤖 Desarrollo con IA

1. Instala una vez las skills [hb-*](https://github.com/Hacybeyker/agent-skills) en tu máquina.
2. Abre tu agente (Claude Code, Copilot, Gemini en Android Studio, Junie…) en la raíz del proyecto y pídele lo que necesites:

> *"Implementa la feature de perfil de usuario."*

`hb-implement` lee `AGENTS.md`, aplica el perfil `hb-stack-mobile-kotlin` (variante `kmp`) y corre el DoD del proyecto, que incluye los tests y el build de iOS; `hb-review` revisa, `hb-commit` prepara los commits y `hb-release` corta las versiones.

## 🏗️ Estructura del proyecto

```
.
├── shared/            # Código compartido (Compose Multiplatform UI + lógica)
│   └── src/
│       ├── commonMain/    # core/, feature/<nombre>/ (domain / data / ui / di), navigation/
│       ├── androidMain/   # Implementaciones de Android (mismos paquetes)
│       └── iosMain/       # Implementaciones de iOS (mismos paquetes)
├── androidApp/        # Entry point de Android (MainActivity)
├── iosApp/            # Entry point de iOS (SwiftUI + Xcode project)
├── AGENTS.md          # Reglas del proyecto para agentes de IA
├── CLAUDE.md          # Importa AGENTS.md para Claude Code
├── init-project.sh    # ⚡ Inicializador del scaffolding
└── SETUP.md           # Guía detallada de inicialización
```

## 🔨 Comandos útiles

```bash
# Compilar la app Android
./gradlew :androidApp:assembleDebug

# Tests del módulo compartido
./gradlew :shared:testAndroidHostTest        # Android
./gradlew :shared:iosSimulatorArm64Test     # iOS Simulator

# Calidad de código (ktlint + detekt + Android Lint)
./gradlew formatAndAnalyze     # formatea y verifica todo
./gradlew checkCodeQuality     # solo verifica (ideal para CI)

# App iOS: abre iosApp/ en Xcode y ejecuta desde ahí
```

## 📄 Licencia

Usa esta plantilla libremente para cualquier proyecto, personal o comercial.

---

Hecho con ❤️ para acelerar el desarrollo **KMP + IA**. Si te sirve, ¡deja una ⭐ en el repo!
