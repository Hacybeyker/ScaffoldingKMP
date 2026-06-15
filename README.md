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
| **`AGENTS.md`** | Fuente de verdad para agentes de IA (estándar [agents.md](https://agents.md/)) |
| **`.agents/`** | Skills de IA: arquitectura KMP, commits semánticos, changelog, creación de skills |
| **Symlinks multi-IDE** | Las skills se sincronizan automáticamente para Claude Code, Copilot, Cursor, JetBrains, Junie y Antigravity |
| **Catálogo de versiones** | `gradle/libs.versions.toml` centralizado (Kotlin 2.4, AGP 9, Compose 1.11) |
| **Calidad de código** | **ktlint + detekt + Android Lint** preconfigurados con tareas agregadas (`./gradlew formatAndAnalyze`) — reglas en `.editorconfig` y `config/detekt/detekt.yml` |
| **CI/CD (GitHub Actions)** | `ci.yml` (calidad + build/tests Android e iOS en cada push/PR) y `release.yml` (APK/AAB + GitHub Release al pushear un tag `v*`) + Dependabot |

## 📋 Requisitos

- **JDK 21** (Gradle lo descarga automáticamente vía toolchain si no lo tienes)
- **Android Studio** (versión reciente con soporte KMP)
- **Xcode** (solo para compilar/ejecutar la app iOS, requiere macOS)

## ⚡ Quick Start

```bash
# 1. Clona la plantilla con el nombre de tu nuevo proyecto
git clone https://github.com/hacybeyker/ScaffoldingKMP.git MiAppGenial
cd MiAppGenial

# 2. Ejecuta el inicializador (modo interactivo)
./init-project.sh
```

El script te preguntará el **nombre del proyecto**, el **package base** y el **nombre visible de la app**, y hará todo el resto: renombrar archivos, mover paquetes, configurar la documentación de IA, crear los symlinks, renombrar la carpeta raíz y dejar el historial de git limpio (squash automático si vienes del scaffolding, o commit encima si vienes de GitHub Template).

¿Prefieres no responder preguntas? Modo no interactivo (ideal para agentes de IA):

```bash
./init-project.sh --name MiAppGenial --package com.empresa.miapp --app-name "Mi App Genial" --yes
```

> 📖 **Guía completa paso a paso:** [SETUP.md](./SETUP.md)

## 🤖 Desarrollo con IA

Una vez inicializado, abre tu agente de IA favorito en la raíz del proyecto y dile:

> *"Lee AGENTS.md y ayúdame a implementar mi primera feature."*

El agente encontrará las reglas de arquitectura (Clean Architecture + MVVM + State/Event/Effect), los estándares de código, la estrategia de testing y la guía de seguridad móvil en `.agents/skills/`.

## 🏗️ Estructura del proyecto

```
.
├── shared/            # Código compartido (Compose Multiplatform UI + lógica)
│   └── src/
│       ├── commonMain/    # Código común a todas las plataformas
│       ├── androidMain/   # Implementaciones específicas de Android
│       └── iosMain/       # Implementaciones específicas de iOS
├── androidApp/        # Entry point de Android (MainActivity)
├── iosApp/            # Entry point de iOS (SwiftUI + Xcode project)
├── AGENTS.md          # Fuente de verdad para agentes de IA
├── .agents/           # Skills e infraestructura de IA
│   ├── skills/        # kmp-best-practices, git-commit, changelog, etc.
│   └── scripts/       # sync-skills.sh (symlinks multi-IDE)
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
