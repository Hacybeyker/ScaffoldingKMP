---
name: kmp-best-practices
description: 'Kotlin Multiplatform (KMP) master skill. Clean Architecture, MVVM + State/Event/Effect, Room, Koin, Ktor, Coil3, and Material 3. Covers all layers, testing strategies, and platform-specific implementations (Android/iOS).'
license: MIT
---

# Kotlin Multiplatform Master Skill — {{PROJECT_NAME}}

Esta es la guía definitiva para implementar funcionalidades en este proyecto. Cualquier implementación debe ser predecible, testeable y alineada con los estándares de producción de Google y JetBrains.

## 🏗️ Project Anatomy

```
{{MODULE_NAME}}/src/commonMain/kotlin/{{PACKAGE_PATH}}/
├── core/                      # Compartido: Database, Network, DataStore, DI, Shared
├── navigation/                # Grafo de navegación Type-safe
└── features/                  # Módulos de funcionalidad (Vertical Slices)
    └── {{feature_name}}/
        ├── presentation/      # ui/screens, viewmodel, state/ (State/Event/Effect)
        ├── domain/            # model, usecase, repository/ (Interfaces)
        └── data/              # repository/ (Impl), source/ (Remote/Local), mapper/
```

> `{{MODULE_NAME}}` es el módulo principal de Compose Multiplatform (por convención `composeApp` o `shared`, pero puede variar según el proyecto).

## 🛠️ Key Technical Patterns

### 1. The ViewModel Contract
```kotlin
class {{Feature}}ViewModel(private val useCase: UseCase) : ViewModel() {
    private val _state = MutableStateFlow({{Feature}}State())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<{{Feature}}Effect>()
    val effect = _effect.receiveAsFlow()
    
    fun onEvent(event: {{Feature}}Event) { /* Business logic stays in UseCase */ }
}
```

### 2. Dependency Injection (Koin)
Uso obligatorio de los DSLs modernos para evitar boilerplate:
```kotlin
val {{feature}}Module = module {
    viewModelOf(::{{Feature}}ViewModel)
    factoryOf(::UseCase)
    singleOf(::RepositoryImpl) bind Repository::class
}
```

### 3. SOLID & Patrones de Diseño
Toda implementación debe respetar los principios SOLID:
- **S**: UseCases y Mappers con una única responsabilidad.
- **O/L**: Modela jerarquías con `sealed interface` y composición, no herencia profunda.
- **I**: Interfaces de Repository pequeñas y específicas por feature.
- **D**: Domain define interfaces; Data las implementa. Todo se inyecta vía Koin.

Patrones de diseño recomendados: **Repository** (acceso a datos), **Factory** (creación de clientes HTTP/DB), **Strategy** (variaciones de lógica), **Observer** (reactividad vía `Flow`/`StateFlow`).

## 📚 Deep Dive Guides
Para una comprensión profunda de cada área, consulta las guías de referencia:

1.  **[Architecture & Workflow](references/ARCHITECTURE_AND_WORKFLOW.md)**: Flujo de 6 pasos para nuevas features.
2.  **[UI & Styling Guide](references/UI_AND_STYLING_GUIDE.md)**: Compose Patterns y Material Design 3.
3.  **[Testing Strategies](references/TESTING_STRATEGIES.md)**: Tests unitarios con Fakes y UI Testing.
4.  **[Platform Implementations](references/PLATFORM_IMPLEMENTATIONS.md)**: Room KMP, Ktor y `expect`/`actual`.
5.  **[Mobile Security Guide](references/MOBILE_SECURITY_GUIDE.md)**: SSL Pinning, secretos, cifrado, ofuscación y hardening Android/iOS.

## ✅ Quality Checklist
- [ ] ¿El código pasa **ktlint** y **detekt**? (usa las tareas de calidad del proyecto, ej. `./gradlew formatAndAnalyze` o `ktlintFormat detekt`)
- [ ] ¿Se han incluido tests unitarios en `commonTest` usando Fakes?
- [ ] ¿Las pantallas nuevas o modificadas tienen screenshot tests actualizados?
- [ ] ¿El Domain es 100% independiente de librerías externas?
- [ ] ¿Se respetan los principios SOLID (UseCases pequeños, dependencias hacia abstracciones)?
- [ ] ¿Se utiliza `libs.versions.toml` para cualquier dependencia nueva?
- [ ] ¿Se cumple el [Security Checklist](references/MOBILE_SECURITY_GUIDE.md) (sin secretos en duro, pinning, almacenamiento cifrado, ofuscación)?

---
**Standard KMP Skill** — Basado en Clean Architecture y patrones de industria.
