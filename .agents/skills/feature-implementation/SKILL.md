---
name: feature-implementation
description: 'Workflow completo de implementación KMP. Úsalo cuando el usuario pida: crear una feature, resolver un bug/issue, aplicar un fix, hacer un enhancement, o refactorizar código. Cubre: (1) nuevas features con todas las capas Clean Architecture (domain → data → DI → presentation), (2) bugfixes con análisis de causa raíz, (3) enhancements, (4) refactors. Siempre verifica el DoD completo (ktlint + detekt + lint + tests Android/iOS + compilación Android/iOS) y genera un reporte HTML. Triggers: "crea la feature X", "fix bug Y", "implementa Z", "enhance/mejora X", "refactoriza Y", "resuelve el issue #N".'
license: MIT
---

# Feature Implementation — KMP Workflow

> **Regla de oro**: Lee `AGENTS.md` antes de escribir una sola línea de código. Toda implementación sigue las reglas allí definidas.

---

## Phase 0 — Context Loading

Carga siempre antes de implementar:

1. Lee `AGENTS.md` (reglas del proyecto, stack, hard rules)
2. Lee `.agents/skills/kmp-best-practices/SKILL.md`
3. Lee `.agents/skills/kmp-best-practices/references/ARCHITECTURE_AND_WORKFLOW.md`
4. Lee `.agents/skills/kmp-best-practices/references/TESTING_STRATEGIES.md`
5. **Si hay cambios de UI**: Lee `references/UI_AND_STYLING_GUIDE.md`
6. **Si hay código nativo/expect-actual**: Lee `references/PLATFORM_IMPLEMENTATIONS.md`
7. **Si hay seguridad (auth, storage, network)**: Lee `references/MOBILE_SECURITY_GUIDE.md`

Determina el tipo de tarea: `feature | bugfix | enhancement | refactor`

---

## Phase 1 — Snapshot (Before)

Antes de modificar cualquier archivo:

- Lee cada archivo que **vas a modificar** y anota su contenido completo como "estado anterior"
- Lista los archivos que **vas a crear** (no tienen estado anterior)
- Lista los archivos que **vas a eliminar** (solo su contenido anterior)

Este snapshot es obligatorio para el reporte HTML.

---

## Phase 2 — Implementation

### 2A. Feature / Enhancement

Sigue el flujo de 6 pasos de `ARCHITECTURE_AND_WORKFLOW.md`:

```
feature/
├── domain/
│   ├── entity/         ← 1. Entidad de dominio (data class inmutable)
│   ├── repository/     ← 2. Interfaz del repositorio
│   └── usecase/        ← 3. UseCase (función invoke)
├── data/
│   ├── model/          ← 4a. DTO interno (internal data class)
│   ├── mapper/         ← 4b. Mapper DTO → Domain (extension fun)
│   └── repository/     ← 4c. RepositoryImpl (internal class)
├── di/
│   └── Module.kt       ← 5. Módulo Koin (viewModelOf, factoryOf, singleOf)
└── presentation/
    ├── state/
    │   ├── State.kt    ← 6a. data class inmutable
    │   ├── Event.kt    ← 6b. sealed interface (acciones usuario)
    │   └── Effect.kt   ← 6c. sealed interface (one-shot: nav, snackbar)
    ├── ViewModel.kt    ← 6d. ViewModel con StateFlow + Channel
    ├── Screen.kt       ← 6e. Composable stateful (koinViewModel)
    └── Content.kt      ← 6f. Composable stateless + Preview en androidMain
```

**Reglas críticas de implementación**:
- DTOs y RepositoryImpl son siempre `internal`
- `@Preview` va en `androidMain`, **no** en `commonMain`
- El ViewModel solo inyecta UseCases, nunca Repositorios directamente
- Usa `Result<T>` para propagar errores del Repositorio al ViewModel
- Registra el módulo Koin en `App.kt` dentro de `KoinApplication`

### 2B. Bug Fix

1. Lee el código afectado y reproduce el bug mentalmente
2. Escribe un test que falle (reproduce el bug) — ANTES de fijar
3. Implementa el fix mínimo necesario
4. Verifica que el test ahora pase
5. Revisa si hay bugs relacionados en el mismo área

### 2C. Refactor

1. Verifica que existen tests para el código a refactorizar; si no, créalos primero
2. Aplica los cambios preservando el comportamiento observable
3. Confirma que todos los tests existentes siguen pasando

---

## Phase 3 — Definition of Done (OBLIGATORIO)

Ejecuta en orden. Si un paso falla: **corrige el error y vuelve a ejecutar ese paso y todos los siguientes**.

```bash
# 1. Formatear y verificar calidad (ktlint + detekt + Android Lint)
./gradlew formatAndAnalyze

# 2. Tests en Android (JVM — siempre disponible)
./gradlew :shared:testAndroidHostTest

# 3. Tests en iOS Simulator (solo si el entorno es Apple Silicon)
./gradlew :shared:iosSimulatorArm64Test

# 4. Compilación Android (debug)
./gradlew :androidApp:assembleDebug

# 5. Compilación iOS (sin firma, para verificar que no hay errores de compilación)
xcodebuild \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -destination "generic/platform=iOS Simulator" \
  CODE_SIGNING_ALLOWED=NO \
  build 2>&1 | grep -E "error:|BUILD SUCCEEDED|BUILD FAILED"
```

**DoD pasa cuando**: todos los pasos terminan con BUILD SUCCESSFUL / BUILD SUCCEEDED.
**Pasos opcionales**: si el entorno no tiene macOS/Xcode, marca los pasos 3 y 5 como `SKIPPED (no macOS)` en el reporte.

---

## Phase 4 — HTML Report Generation

Al terminar el DoD, genera el reporte:

1. **Destino**: `reports/YYYY-MM-DD-{task-slug}.html`  
   Ejemplo: `reports/2026-06-12-user-profile-feature.html`

2. **Agrega `reports/` al `.gitignore`** si no está ya presente

3. **Escribe el HTML** siguiendo exactamente la estructura de `assets/report-template.html`

4. **Contenido requerido** (ver template para el HTML exacto):

| Sección | Contenido |
|---------|-----------|
| **Header** | Nombre del proyecto, tipo de tarea (badge), fecha, descripción |
| **Resumen** | Qué se hizo, por qué, scope (feature name), archivos totales |
| **Archivos Nuevos** | Por cada archivo nuevo: ruta + contenido completo con syntax highlight |
| **Archivos Modificados** | Por cada archivo modificado: ruta + bloque ANTES + bloque DESPUÉS |
| **Archivos Eliminados** | Por cada archivo borrado: ruta + contenido previo |
| **DoD** | Cada paso con ícono ✅ PASS / ❌ FAIL / ⏭️ SKIPPED y output relevante |
| **Footer** | Proyecto, fecha, generado por Claude Code |

5. **Al final**, informa al usuario la ruta del reporte:
   ```
   📄 Reporte generado: reports/YYYY-MM-DD-{task-slug}.html
   ```

---

## Phase 5 — Final Handoff

```
⛔ NO hagas commit automático — el usuario debe revisar los cambios primero.
```

Presenta al usuario:
- Resumen de qué se implementó (2-3 líneas)
- Lista de archivos creados / modificados
- Resultado del DoD (PASS / FAIL / SKIPPED por paso)
- Ruta del reporte HTML
- Comando de commit sugerido (para que el usuario lo ejecute manualmente)

---

## Quick Reference — Checklist

- [ ] `AGENTS.md` y referencias de `kmp-best-practices` leídas
- [ ] Snapshot "before" capturado para todos los archivos a modificar
- [ ] Arquitectura Clean Architecture respetada (domain ← data, presentation → domain)
- [ ] DTOs e implementaciones de repositorio son `internal`
- [ ] Tests escritos en `commonTest` usando Fakes (no mocks)
- [ ] `@Preview` en `androidMain`, no en `commonMain`
- [ ] Módulo Koin registrado en `App.kt`
- [ ] `./gradlew formatAndAnalyze` — PASS
- [ ] Tests Android — PASS
- [ ] Tests iOS — PASS / SKIPPED
- [ ] Compilación Android — PASS
- [ ] Compilación iOS — PASS / SKIPPED
- [ ] Reporte HTML generado en `reports/`
- [ ] **NO commit automático**
