#!/bin/bash
# setup-ai.sh — Inicializador de Infraestructura de IA para Proyectos KMP

set -e

echo "🤖 Bienvenido al Inicializador de IA para Kotlin Multiplatform"
echo "------------------------------------------------------------"

# 1. Recopilar información
read -p "📌 Nombre del Proyecto (ej. MiAppGenial): " PROJECT_NAME
read -p "📦 Package Name base (ej. com.ejemplo.app): " PACKAGE_NAME
read -p "🧩 Módulo principal de Compose [composeApp]: " MODULE_NAME
MODULE_NAME=${MODULE_NAME:-composeApp}

# Validar entradas
if [[ -z "$PROJECT_NAME" || -z "$PACKAGE_NAME" ]]; then
    echo "❌ Error: El nombre del proyecto y el package name son obligatorios."
    exit 1
fi

PROJECT_ROOT=$(pwd)
PACKAGE_PATH=$(echo "$PACKAGE_NAME" | tr '.' '/')

echo ""
echo "⚙️  Configurando con los siguientes valores:"
echo "   - Proyecto: $PROJECT_NAME"
echo "   - Package:  $PACKAGE_NAME"
echo "   - Ruta:     $PACKAGE_PATH"
echo "   - Módulo:   $MODULE_NAME"
echo "   - Root:     $PROJECT_ROOT"
echo ""

# 2. Función para reemplazar placeholders (Soporte macOS y Linux)
replace_placeholders() {
    local file=$1
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS sed requiere una cadena vacía para -i
        sed -i '' "s/{{PROJECT_NAME}}/$PROJECT_NAME/g" "$file"
        sed -i '' "s/{{PACKAGE_NAME}}/$PACKAGE_NAME/g" "$file"
        sed -i '' "s/{{MODULE_NAME}}/$MODULE_NAME/g" "$file"
        sed -i '' "s|{{PACKAGE_PATH}}|$PACKAGE_PATH|g" "$file"
        sed -i '' "s|{{PROJECT_ROOT}}|$PROJECT_ROOT|g" "$file"
    else
        # Linux / Windows Git Bash
        sed -i "s/{{PROJECT_NAME}}/$PROJECT_NAME/g" "$file"
        sed -i "s/{{PACKAGE_NAME}}/$PACKAGE_NAME/g" "$file"
        sed -i "s/{{MODULE_NAME}}/$MODULE_NAME/g" "$file"
        sed -i "s|{{PACKAGE_PATH}}|$PACKAGE_PATH|g" "$file"
        sed -i "s|{{PROJECT_ROOT}}|$PROJECT_ROOT|g" "$file"
    fi
}

# 3. Procesar archivos
echo "📝 Reemplazando placeholders en archivos de documentación..."

# Procesar AGENTS.md en la raíz
if [ -f "AGENTS.md" ]; then
    replace_placeholders "AGENTS.md"
fi

# Procesar todos los archivos dentro de .agents/ de forma recursiva
find .agents -type f \( -name "*.md" -o -name "*.sh" \) | while read -r file; do
    replace_placeholders "$file"
done

echo "   ✅ Documentación actualizada."

# 4. Vincular Skills con los IDEs (Symlinks)
echo "🔗 Configurando symlinks para los editores..."
if [ -f ".agents/scripts/sync-skills.sh" ]; then
    chmod +x .agents/scripts/sync-skills.sh
    ./.agents/scripts/sync-skills.sh
else
    echo "⚠️  Advertencia: No se encontró el script de symlinks en .agents/scripts/sync-skills.sh"
fi

echo ""
echo "------------------------------------------------------------"
echo "🎉 ¡Infraestructura de IA lista para $PROJECT_NAME!"
echo "------------------------------------------------------------"
echo "🚀 Siguientes pasos:"
echo "   1. Verifica que gradle/libs.versions.toml tenga tus dependencias."
echo "   2. Dile a tu IA: 'Lee AGENTS.md y ayúdame a implementar mi primera feature'."
echo "------------------------------------------------------------"
