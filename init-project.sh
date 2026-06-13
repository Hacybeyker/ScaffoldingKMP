#!/bin/bash
# init-project.sh — Inicializador del Scaffolding KMP
#
# Convierte este scaffolding en TU proyecto: renombra el proyecto, el package,
# el applicationId, el bundle id de iOS y configura la infraestructura de IA.
#
# Uso interactivo:
#   ./init-project.sh
#
# Uso no interactivo (ideal para agentes de IA / CI):
#   ./init-project.sh --name MiApp --package com.empresa.miapp [--app-name "Mi App"] [--module shared] [--yes]
#
# Flags:
#   -n, --name      Nombre del proyecto (PascalCase, sin espacios). Ej: MiAppGenial
#   -p, --package   Package base. Ej: com.empresa.miapp
#   -a, --app-name  Nombre visible de la app (puede tener espacios). Default: igual a --name
#   -m, --module    Módulo principal de Compose. Default: shared
#   -y, --yes       Responde "sí" a todas las confirmaciones (limpieza + reset de git)
#   -h, --help      Muestra esta ayuda

set -e

# ── Valores actuales del scaffolding (NO modificar) ─────────────────────────
OLD_PROJECT_NAME="ScaffoldingKMP"
OLD_PACKAGE_NAME="com.hacybeyker.scaffoldingkmp"

SCRIPT_NAME="$(basename "$0")"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# ── Helpers ──────────────────────────────────────────────────────────────────
sedi() {
    # sed -i portable (macOS requiere sufijo vacío)
    if [[ "$OSTYPE" == darwin* ]]; then
        sed -i '' "$@"
    else
        sed -i "$@"
    fi
}

usage() {
    sed -n '2,18p' "$0" | sed 's/^# \{0,1\}//'
    exit 0
}

die() {
    echo "❌ Error: $1" >&2
    exit 1
}

# ── 1. Recopilar información ─────────────────────────────────────────────────
PROJECT_NAME=""
PACKAGE_NAME=""
APP_NAME=""
MODULE_NAME=""
ASSUME_YES=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        -n|--name)     PROJECT_NAME="$2"; shift 2 ;;
        -p|--package)  PACKAGE_NAME="$2"; shift 2 ;;
        -a|--app-name) APP_NAME="$2"; shift 2 ;;
        -m|--module)   MODULE_NAME="$2"; shift 2 ;;
        -y|--yes)      ASSUME_YES=true; shift ;;
        -h|--help)     usage ;;
        *)             die "Flag desconocida: $1 (usa --help)" ;;
    esac
done

echo "🚀 Scaffolding KMP — Inicializador de Proyecto"
echo "------------------------------------------------------------"

if [[ -z "$PROJECT_NAME" ]]; then
    read -r -p "📌 Nombre del Proyecto (PascalCase, ej. MiAppGenial): " PROJECT_NAME
fi
if [[ -z "$PACKAGE_NAME" ]]; then
    read -r -p "📦 Package base (ej. com.empresa.miapp): " PACKAGE_NAME
fi
if [[ -z "$APP_NAME" && "$ASSUME_YES" == false ]]; then
    read -r -p "🏷️  Nombre visible de la app [$PROJECT_NAME]: " APP_NAME
fi
if [[ -z "$MODULE_NAME" && "$ASSUME_YES" == false ]]; then
    read -r -p "🧩 Módulo principal de Compose [shared]: " MODULE_NAME
fi
APP_NAME=${APP_NAME:-$PROJECT_NAME}
MODULE_NAME=${MODULE_NAME:-shared}

# ── 2. Validaciones ──────────────────────────────────────────────────────────
[[ -n "$PROJECT_NAME" && -n "$PACKAGE_NAME" ]] || die "El nombre del proyecto y el package son obligatorios."

[[ "$PROJECT_NAME" =~ ^[A-Za-z][A-Za-z0-9_]*$ ]] \
    || die "Nombre de proyecto inválido: '$PROJECT_NAME'. Usa solo letras/números, sin espacios (ej. MiAppGenial)."

[[ "$PACKAGE_NAME" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]] \
    || die "Package inválido: '$PACKAGE_NAME'. Usa minúsculas separadas por puntos (ej. com.empresa.miapp)."

[[ "$PACKAGE_NAME" != "$OLD_PACKAGE_NAME" ]] \
    || die "El package no puede ser el mismo del scaffolding ($OLD_PACKAGE_NAME)."

grep -rq "$OLD_PACKAGE_NAME" settings.gradle.kts shared androidApp 2>/dev/null \
    || die "Este scaffolding ya fue inicializado (no quedan referencias a $OLD_PACKAGE_NAME)."

OLD_PACKAGE_PATH=$(echo "$OLD_PACKAGE_NAME" | tr '.' '/')
NEW_PACKAGE_PATH=$(echo "$PACKAGE_NAME" | tr '.' '/')
OLD_PACKAGE_RE=${OLD_PACKAGE_NAME//./\\.}

echo ""
echo "⚙️  Se configurará el proyecto con:"
echo "   - Proyecto:    $PROJECT_NAME"
echo "   - App visible: $APP_NAME"
echo "   - Package:     $PACKAGE_NAME"
echo "   - Módulo:      $MODULE_NAME"
echo "   - Root:        $PROJECT_ROOT"
echo ""

if [[ "$ASSUME_YES" == false ]]; then
    read -r -p "¿Continuar? [Y/n]: " CONFIRM
    [[ -z "$CONFIRM" || "$CONFIRM" =~ ^[Yy] ]] || { echo "Cancelado."; exit 0; }
fi

# ── 3. Reemplazo en archivos de texto ────────────────────────────────────────
# Excluye binarios (-I), directorios generados y archivos que se sobrescriben
# íntegramente al final del script:
#   README.md    → regenerado en el paso 7  (queda en el proyecto nuevo)
#   CHANGELOG.md → regenerado en el paso 7b (queda en el proyecto nuevo)
#   SETUP.md     → eliminado en el paso 8   (era solo para el scaffolding)
#   $SCRIPT_NAME → eliminado en el paso 8   (era solo para el scaffolding)
replace_in_repo() {
    local pattern=$1
    local replacement=$2
    grep -rIl \
        --exclude-dir=.git --exclude-dir=.gradle --exclude-dir=.kotlin \
        --exclude-dir=.idea --exclude-dir=build --exclude-dir=xcuserdata \
        --exclude-dir=.konan --exclude-dir=DerivedData \
        --exclude="$SCRIPT_NAME" --exclude="README.md" --exclude="SETUP.md" --exclude="CHANGELOG.md" \
        "$pattern" . 2>/dev/null | while read -r file; do
        sedi "s|$pattern|$replacement|g" "$file"
        echo "   ✏️  $file"
    done
}

echo "🏷️  Configurando nombre visible de la app (Android)..."
sedi "s|<string name=\"app_name\">.*</string>|<string name=\"app_name\">$APP_NAME</string>|" \
    "androidApp/src/main/res/values/strings.xml"

echo "📦 Renombrando package: $OLD_PACKAGE_NAME → $PACKAGE_NAME"
replace_in_repo "$OLD_PACKAGE_RE" "$PACKAGE_NAME"

echo "📛 Renombrando proyecto: $OLD_PROJECT_NAME → $PROJECT_NAME"
replace_in_repo "$OLD_PROJECT_NAME" "$PROJECT_NAME"

# El package de recursos generados por Compose deriva del nombre del proyecto
# en minúsculas (ej. scaffoldingkmp.shared.generated.resources.Res)
OLD_NAME_LOWER=$(echo "$OLD_PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
NEW_NAME_LOWER=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
if [[ "$OLD_NAME_LOWER" != "$NEW_NAME_LOWER" ]]; then
    echo "🎨 Actualizando imports de Compose Resources: $OLD_NAME_LOWER → $NEW_NAME_LOWER"
    replace_in_repo "$OLD_NAME_LOWER" "$NEW_NAME_LOWER"
fi

# ── 4. Mover directorios de código fuente al nuevo package ──────────────────
echo "📁 Moviendo directorios de código al nuevo package..."
find . -type d -path "*/kotlin/$OLD_PACKAGE_PATH" \
    -not -path "./.git/*" -not -path "*/build/*" | while read -r old_dir; do
    base="${old_dir%/"$OLD_PACKAGE_PATH"}"
    new_dir="$base/$NEW_PACKAGE_PATH"
    [[ "$old_dir" == "$new_dir" ]] && continue
    mkdir -p "$new_dir"
    # Mueve todo el contenido (archivos y subcarpetas de features)
    find "$old_dir" -mindepth 1 -maxdepth 1 -exec mv {} "$new_dir"/ \;
    echo "   ✅ $new_dir"
done

# Elimina los directorios vacíos que dejó el package anterior
OLD_TOP_SEGMENT=$(echo "$OLD_PACKAGE_NAME" | cut -d. -f1)
find . -type d -path "*/kotlin/$OLD_TOP_SEGMENT" -not -path "./.git/*" -not -path "*/build/*" \
    -exec find {} -depth -type d -empty -delete \; 2>/dev/null || true

# ── 4.5. Limpiar ejemplo del scaffolding (feature/platforminfo + Greeting) ────
echo "🗑️  Eliminando código de ejemplo del scaffolding..."
# Derive the new package path (already set above as NEW_PACKAGE_PATH)
SHARED_SRC="shared/src"
for src_set in commonMain commonTest androidHostTest iosTest iosSimulatorArm64Test; do
    FEATURE_DIR="$SHARED_SRC/$src_set/kotlin/$NEW_PACKAGE_PATH/feature"
    if [[ -d "$FEATURE_DIR" ]]; then
        rm -rf "$FEATURE_DIR"
        echo "   ✅ Eliminado $FEATURE_DIR"
    fi
done
# Remove legacy Greeting files
for src_set in commonMain commonTest androidHostTest iosTest; do
    PKG_DIR="$SHARED_SRC/$src_set/kotlin/$NEW_PACKAGE_PATH"
    rm -f "$PKG_DIR/Greeting.kt" "$PKG_DIR/GreetingUtil.kt" \
          "$PKG_DIR/GreetingTest.kt" "$PKG_DIR/GreetingUtilTest.kt"
done
# Restore a clean App.kt
APP_KT="$SHARED_SRC/commonMain/kotlin/$NEW_PACKAGE_PATH/App.kt"
if [[ -f "$APP_KT" ]]; then
    cat > "$APP_KT" <<APPEOF
package $PACKAGE_NAME

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun App() {
    MaterialTheme {
        // TODO: add your first screen here
    }
}
APPEOF
    echo "   ✅ App.kt restaurado limpio."
fi
echo "   ✅ Ejemplo del scaffolding eliminado."

# ── 5. Placeholders en la documentación de IA ────────────────────────────────
echo "📝 Configurando documentación de IA (AGENTS.md y .agents/)..."
replace_placeholders() {
    local file=$1
    sedi "s|{{PROJECT_NAME}}|$PROJECT_NAME|g" "$file"
    sedi "s|{{PACKAGE_NAME}}|$PACKAGE_NAME|g" "$file"
    sedi "s|{{MODULE_NAME}}|$MODULE_NAME|g" "$file"
    sedi "s|{{PACKAGE_PATH}}|$NEW_PACKAGE_PATH|g" "$file"
    sedi "s|{{PROJECT_ROOT}}|$PROJECT_NAME|g" "$file"
}

[[ -f "AGENTS.md" ]] && replace_placeholders "AGENTS.md"
find .agents -type f \( -name "*.md" -o -name "*.sh" \) | while read -r file; do
    replace_placeholders "$file"
done
echo "   ✅ Documentación actualizada."

# ── 6. Symlinks de skills para los IDEs ──────────────────────────────────────
echo "🔗 Sincronizando skills con los editores..."
if [[ -f ".agents/scripts/sync-skills.sh" ]]; then
    chmod +x .agents/scripts/sync-skills.sh
    ./.agents/scripts/sync-skills.sh
else
    echo "⚠️  No se encontró .agents/scripts/sync-skills.sh — omitido."
fi

# ── 7. README del nuevo proyecto ─────────────────────────────────────────────
echo "📄 Generando README.md del nuevo proyecto..."
# GITHUB_USER se puede pasar como variable de entorno; si no existe usa el placeholder.
GH_USER="${GITHUB_USER:-TU_USUARIO}"
cat > README.md <<EOF
# $APP_NAME

<!-- TODO: reemplaza TU_USUARIO con tu usuario u organización de GitHub -->
[![CI](https://github.com/$GH_USER/$PROJECT_NAME/actions/workflows/ci.yml/badge.svg)](https://github.com/$GH_USER/$PROJECT_NAME/actions/workflows/ci.yml)
[![Release](https://github.com/$GH_USER/$PROJECT_NAME/actions/workflows/release.yml/badge.svg)](https://github.com/$GH_USER/$PROJECT_NAME/actions/workflows/release.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.11.1-4285F4?logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-minSdk_24-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-16%2B-000000?logo=apple&logoColor=white)

> <!-- TODO: escribe aquí una descripción breve de tu proyecto -->
> *Descripción del proyecto*

---

## 📁 Estructura

\`\`\`
.
├── shared/            # Código compartido — Compose Multiplatform UI + lógica común
│   └── src/
│       ├── commonMain/    # Código común a todas las plataformas
│       ├── androidMain/   # Implementaciones específicas de Android
│       └── iosMain/       # Implementaciones específicas de iOS
├── androidApp/        # Entry point Android (MainActivity)
├── iosApp/            # Entry point iOS (SwiftUI + Xcode project)
├── AGENTS.md          # Fuente de verdad para agentes de IA
├── .agents/           # Skills e infraestructura de IA
└── gradle/
    └── libs.versions.toml  # Catálogo centralizado de versiones
\`\`\`

---

## ⚡ Ejecutar

| Plataforma | Comando |
|-----------|---------|
| Android   | \`./gradlew :androidApp:assembleDebug\` o Run desde Android Studio |
| iOS       | Abre \`iosApp/\` en Xcode → selecciona simulador → ▶ |

> **iOS:** configura tu Team ID en \`iosApp/Configuration/Config.xcconfig\` antes de compilar en dispositivo físico.

---

## 🧪 Tests

\`\`\`bash
# Tests Android (JVM — sin emulador)
./gradlew :shared:testAndroidHostTest

# Tests iOS (requiere Apple Silicon + Xcode)
./gradlew :shared:iosSimulatorArm64Test
\`\`\`

---

## 🎨 Calidad de código

ktlint + detekt + Android Lint están preconfigurados. Reglas en \`.editorconfig\` y \`config/detekt/detekt.yml\`.

\`\`\`bash
# Antes de cada commit: formatea y verifica todo
./gradlew formatAndAnalyze

# Solo verificación (CI / pre-push)
./gradlew checkCodeQuality
\`\`\`

El proyecto incluye un **pre-commit hook** que ejecuta \`formatAndAnalyze\` automáticamente antes de cada commit. Instálalo una sola vez tras clonar:

\`\`\`bash
chmod +x scripts/setup-quality-hook.sh && ./scripts/setup-quality-hook.sh
\`\`\`

---

## 🤖 Desarrollo con IA

Este proyecto incluye infraestructura para agentes de IA (Claude Code, Copilot, Cursor, Junie, Antigravity…).

Comienza diciéndole a tu agente:

> *"Lee AGENTS.md y ayúdame a implementar mi primera feature."*

El agente encontrará las reglas de Clean Architecture, el estándar de código y la guía de testing en \`.agents/skills/\`.

---

## 📄 Changelog

Ver [CHANGELOG.md](./CHANGELOG.md) para el historial de cambios.
EOF
echo "   ✅ README.md generado."

# ── 7b. CHANGELOG del nuevo proyecto ─────────────────────────────────────────
echo "📝 Generando CHANGELOG.md del nuevo proyecto..."
INIT_DATE=$(date +%Y-%m-%d)
cat > CHANGELOG.md <<EOF
# Changelog — $APP_NAME

> Todos los cambios notables de este proyecto están documentados aquí.
> Formato basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/).

---

## [Unreleased]

### ✨ Added
- Proyecto inicializado a partir de [ScaffoldingKMP](https://github.com/hacybeyker/ScaffoldingKMP)

---

<!-- Ejemplo de entrada:

## [1.0.0] — $INIT_DATE

### ✨ Added
- Feature X implementada con Clean Architecture (domain → data → presentation)

### 🔧 Fixed
- Bug Y corregido en el módulo shared

### ♻️ Changed
- Refactor de Z para mejorar legibilidad

### 🗑️ Removed
- Eliminado código obsoleto de A

-->

[Unreleased]: https://github.com/TU_ORG/$PROJECT_NAME/compare/v1.0.0...HEAD
EOF
echo "   ✅ CHANGELOG.md generado."

# ── 8. Limpieza de archivos del scaffolding ──────────────────────────────────
CLEANUP="n"
if [[ "$ASSUME_YES" == true ]]; then
    CLEANUP="y"
else
    read -r -p "🧹 ¿Eliminar archivos del scaffolding (SETUP.md y $SCRIPT_NAME)? [Y/n]: " CLEANUP
    CLEANUP=${CLEANUP:-y}
fi
if [[ "$CLEANUP" =~ ^[Yy] ]]; then
    rm -f SETUP.md
    rm -f -- "$SCRIPT_NAME"
    echo "   ✅ Archivos del scaffolding eliminados."
fi

# ── 9. Reiniciar historial de git ────────────────────────────────────────────
GIT_RESET="n"
if [[ -d ".git" ]]; then
    if [[ "$ASSUME_YES" == true ]]; then
        GIT_RESET="y"
    else
        read -r -p "🌱 ¿Reiniciar historial de git para empezar de cero? [Y/n]: " GIT_RESET
        GIT_RESET=${GIT_RESET:-y}
    fi
    if [[ "$GIT_RESET" =~ ^[Yy] ]]; then
        rm -rf .git
        git init -q
        git add -A
        git commit -q -m "feat: initial commit from ScaffoldingKMP template ($PROJECT_NAME)"
        echo "   ✅ Historial reiniciado con un commit inicial."
    fi
fi

# ── 10. Instalar pre-commit hook ─────────────────────────────────────────────
# Se ejecuta después del posible git reset para que el hook quede en el .git
# recién creado. El script se mantiene en el repo para que futuros colaboradores
# que clonen el proyecto puedan instalarlo con: ./scripts/setup-quality-hook.sh
if [[ -f "scripts/setup-quality-hook.sh" ]]; then
    chmod +x scripts/setup-quality-hook.sh
    ./scripts/setup-quality-hook.sh
fi

# ── 11. Resumen final ────────────────────────────────────────────────────────
echo ""
echo "------------------------------------------------------------"
echo "🎉 ¡$PROJECT_NAME está listo!"
echo "------------------------------------------------------------"
echo "🚀 Siguientes pasos:"
echo "   1. Si la carpeta raíz aún se llama '$OLD_PROJECT_NAME', renómbrala:"
echo "      cd .. && mv $OLD_PROJECT_NAME $PROJECT_NAME"
echo "   2. Abre el proyecto en Android Studio y sincroniza Gradle."
echo "   3. iOS: abre iosApp/ en Xcode y configura tu TEAM_ID en"
echo "      iosApp/Configuration/Config.xcconfig (firma de la app)."
echo "   4. Verifica el build: ./gradlew :androidApp:assembleDebug"
echo "   5. Nuevos colaboradores: ejecuta ./scripts/setup-quality-hook.sh una vez tras clonar."
echo "   6. Dile a tu IA: 'Lee AGENTS.md y ayúdame a implementar mi primera feature'."
echo "------------------------------------------------------------"
