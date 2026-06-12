# 🤖 AI Agent Infrastructure — {{PROJECT_NAME}}

> **Configuración universal siguiendo el estándar de [Agent Skills](https://agentskills.io/home)**

## 📋 Resumen
Este directorio es el sistema operativo de cualquier IA que trabaje en este proyecto. Proporciona las reglas, el estilo de código y los flujos de trabajo necesarios para garantizar que el desarrollo en **Kotlin Multiplatform** sea consistente y de alta calidad.

---

## 📦 Skills de IA Disponibles

| Skill | Activación | Función |
|-------|------------|---------|
| `kmp-best-practices` | *Automática* | **Guía Maestra.** Define arquitectura, capas y estándares técnicos. |
| `git-commit` | `/commit` | Genera mensajes de commit semánticos y profesionales. |
| `skill-creator` | `/new-skill` | Permite a la IA crear nuevas reglas específicas para este proyecto. |
| `changelog-generator` | `/changelog` | Genera notas de lanzamiento automáticas. |
| `skill-linker` | *Automática* | Sincroniza las skills con todos los editores vía symlinks. |

---

## 🏗️ Arquitectura de la "Fuente de Verdad"

Para evitar la fragmentación entre IDEs (VS Code, Android Studio, Claude Code), utilizamos **Symlinks**.

```
{{PROJECT_ROOT}}/
├── .agents/skills/           # ✨ FUENTE DE VERDAD (Originales)
├── .github/copilot/skills/   # → (Symlink) para VS Code / Copilot
├── .jetbrains/agent/skills/  # → (Symlink) para Android Studio / IntelliJ
├── .claude/skills/           # → (Symlink) para Claude Code
├── .cursor/skills/           # → (Symlink) para Cursor
├── .junie/skills/            # → (Symlink) para Junie
├── .antigravity/skills/      # → (Symlink) para Antigravity
└── .agent/skills/            # → (Symlink) genérico (otros agentes)
```

> [!IMPORTANT]
> **Regla de Oro:** No modifiques los symlinks de los editores (`.github/`, `.jetbrains/`, `.claude/`, etc.). Los cambios se hacen **siempre** en `.agents/skills/` y se sincronizan con el script `sync-skills.sh`.

---

## 🚀 Setup para Proyectos Nuevos

1. **Copia** la carpeta `.agents/`, el archivo `AGENTS.md` y el script `setup-ai.sh` a la raíz de tu proyecto KMP.
2. **Inicializa**: Ejecuta el script interactivo, que reemplaza los placeholders (`{{PROJECT_NAME}}`, `{{PACKAGE_NAME}}`, `{{MODULE_NAME}}`) y crea los symlinks automáticamente:
   ```bash
   ./setup-ai.sh
   ```
   > Si prefieres hacerlo manual: actualiza los placeholders en `AGENTS.md`, este `README.md` y `.agents/skills/` y luego ejecuta `./.agents/scripts/sync-skills.sh`.
3. **Validación**: Antes de cada commit, pide a la IA ejecutar las tareas de calidad del proyecto (ej. `./gradlew ktlintFormat detekt`, o la tarea agregada `formatAndAnalyze` si está definida en el `build.gradle.kts` raíz).

---

## 💡 Consejos de Colaboración
- **Usa la IA como Arquitecto**: Si un patrón en la guía `kmp-best-practices` causa fricción, pide a la IA: *"Propón una mejora para esta skill basada en el nuevo patrón que implementamos"*.
- **Mantén las Reglas Claras**: Una IA trabaja mejor con prohibiciones explícitas (ej: "No uses wildcard imports").

---
**Standard Agent Infrastructure** — {{PROJECT_NAME}}
