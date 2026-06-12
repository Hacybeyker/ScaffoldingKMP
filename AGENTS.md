# AGENTS.md — {{PROJECT_NAME}}

> Este archivo sigue el estándar [agents.md](https://agents.md/) y es la **Fuente de Verdad** para agentes de IA en este proyecto **Kotlin Multiplatform (KMP)**.

---

## 🛠️ Tech Stack & Source of Truth

> [!IMPORTANT]
> No asumas versiones. Consulta **SIEMPRE** `gradle/libs.versions.toml` para dependencias y versiones de plugins.

| Categoría | Estándar Sugerido |
|-----------|-------------------|
| **UI** | Compose Multiplatform + Material Design 3 |
| **Arquitectura** | Clean Architecture + MVVM + State/Event/Effect |
| **Navegación** | Compose Navigation con rutas type-safe (`@Serializable`) |
| **DI / Network** | Koin / Ktor Client |
| **Persistencia Local** | Room (KMP) para datos estructurados + DataStore para preferencias |
| **Concurrencia** | Kotlin Coroutines + Flow (`StateFlow` en ViewModels) |
| **Testing** | kotlin-test + Turbine + Fakes (unitarios) / Screenshot Testing (UI) |
| **Principios** | SOLID + Patrones de Diseño (Repository, Factory, Observer, etc.) |
| **Calidad** | ktlint + detekt |

---

## 🏗️ Project Architecture Rules

### 1. Dependencias Unidireccionales
`Presentation (UI/VM) → Domain (UseCases/Entities) ← Data (Repo Impl/Sources)`
- **Domain**: Kotlin puro. Prohibido depender de frameworks (Android, Room, Ktor).
- **ViewModels**: Inyectar UseCases. **Prohibido** inyectar Repositorios directamente.

### 2. State management (MVI Lite)
Cada feature debe ser dirigida por un estado inmutable definido en `presentation/state/`:
- **State**: `data class` inmutable.
- **Event**: `sealed interface` para acciones del usuario.
- **Effect**: `sealed interface` para acciones de una sola vez (navegación, errores).

### 3. Coding Standards
- **Composables**: PascalCase. Separar logic (Screen) de UI (Content).
- **Strings**: Prohibido hardcodear. Usar `Res.string` en `composeResources`.
- **Imports**: Prohibido el uso de wildcards (`import .*`).

### 4. SOLID & Patrones de Diseño
- **SRP**: Una clase, una responsabilidad (UseCases pequeños, un Mapper por transformación).
- **DIP**: Las capas superiores dependen de abstracciones (interfaces de Repository en Domain).
- **OCP/ISP/LSP**: Prefiere `sealed interface` y composición sobre herencia.
- Aplica patrones de diseño donde aporten claridad (Repository, Factory, Strategy, Observer vía `Flow`), nunca por moda.

---

## 🚀 AI Interaction Workflow

Cualquier agente de IA que trabaje en este proyecto **DEBE** seguir estas guías maestras:

1. **Arquitectura & Workflow**: [ARCHITECTURE_AND_WORKFLOW.md](.agents/skills/kmp-best-practices/references/ARCHITECTURE_AND_WORKFLOW.md)
2. **Guía de UI & Styling**: [UI_AND_STYLING_GUIDE.md](.agents/skills/kmp-best-practices/references/UI_AND_STYLING_GUIDE.md)
3. **Calidad & Testing**: [TESTING_STRATEGIES.md](.agents/skills/kmp-best-practices/references/TESTING_STRATEGIES.md)
4. **Implementación de Plataforma**: [PLATFORM_IMPLEMENTATIONS.md](.agents/skills/kmp-best-practices/references/PLATFORM_IMPLEMENTATIONS.md)
5. **Seguridad Móvil**: [MOBILE_SECURITY_GUIDE.md](.agents/skills/kmp-best-practices/references/MOBILE_SECURITY_GUIDE.md)

---

## 🚫 Prohibiciones Críticas (Hard Rules)
- ❌ **NO** omitas la capa de dominio (UseCases).
- ❌ **NO** expongas DTOs fuera de la capa de datos (mapear siempre a Domain).
- ❌ **NO** implementes lógica de negocio en Composables o ViewModels.
- ❌ **NO** uses `@Preview` en funciones de Screen (solo en Content con fakes).
- ❌ **NO** hardcodees secretos (API keys, tokens, passwords) ni guardes credenciales en texto plano. Ver [Guía de Seguridad](.agents/skills/kmp-best-practices/references/MOBILE_SECURITY_GUIDE.md).

---
**Standard KMP Config** — {{PROJECT_NAME}}
