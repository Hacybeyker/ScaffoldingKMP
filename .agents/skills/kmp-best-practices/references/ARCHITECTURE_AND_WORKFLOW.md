# Architecture & Feature Workflow for KMP

Esta guía define cómo estructurar y construir funcionalidades siguiendo **Clean Architecture** y el patrón **Vertical Slices**.

## 🏗️ Clean Architecture Layers

### 1. Domain Layer (El Cerebro)
Lógica de negocio pura. Sin conocimiento de bases de datos o APIs.
- **Entities**: Data classes inmutables.
- **Use Cases**: Una clase por responsabilidad (función `invoke`).
- **Repository Interface**: Define qué datos necesitamos, no cómo se obtienen.

```kotlin
// domain/model/User.kt
data class User(val id: String, val name: String)

// domain/usecase/GetUserUseCase.kt
class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<User> = repository.getUser()
}
```

### 2. Data Layer (Los Músculos)
Implementación técnica y gestión de datos.
- **Repository Impl**: Orquesta entre `Remote` (Ktor) y `Local` (Room).
- **Mappers**: Transforman `DTO` (Data Transfer Object) a `Domain Model`.

### 3. Presentation Layer (La Cara)
UI y orquestación de estado.
- **ViewModel**: Expone un único flujo de estado (`StateFlow`).
- **Composables**: Divididos en `Screen` (stateful) y `Content` (stateless).

---

## 🚀 Workflow de Implementación (6 Pasos)

1.  **Modelado**: Crea la entidad en `domain/model/`.
2.  **Contrato**: Define la interfaz del repositorio en `domain/repository/`.
3.  **Lógica**: Implementa el `UseCase` en `domain/usecase/`.
4.  **Datos**: 
    - Crea DTOs en `data/source/remote/dto/`.
    - Implementa el Repositorio en `data/repository/`.
    - Añade Mappers en `data/mapper/`.
5.  **Estado**: Define `State`, `Event` y `Effect` en `presentation/state/`.
6.  **UI**: Crea el Composable y regístralo en el grafo de navegación.

---

## 🧭 Navegación Type-Safe

Las rutas se definen como objetos/clases `@Serializable` en `navigation/` y se registran en un único grafo. Las pantallas **no** conocen el `NavController`: la navegación se dispara desde los `Effect` del ViewModel.

```kotlin
// navigation/Routes.kt
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val id: String)

// navigation/AppNavGraph.kt
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { HomeScreen(onNavigateToDetail = { navController.navigate(DetailRoute(it)) }) }
    composable<DetailRoute> { backStack -> DetailScreen(backStack.toRoute<DetailRoute>().id) }
}
```

---

## 🧱 Principios SOLID en la Práctica

| Principio | Aplicación en este stack |
|-----------|--------------------------|
| **SRP** | Un UseCase = una acción de negocio. Un Mapper = una transformación. |
| **OCP** | Nuevos comportamientos vía nuevas implementaciones de interfaces, no modificando código existente. |
| **LSP** | Los Fakes de test deben cumplir el mismo contrato que las implementaciones reales. |
| **ISP** | Repositorios por feature; evita interfaces "Dios" con decenas de métodos. |
| **DIP** | Domain define las interfaces; Data/Presentation dependen de ellas vía Koin. |

**Patrones de diseño habituales**: Repository, Factory (HttpClient/Database builders), Strategy (políticas intercambiables), Observer (`Flow`), Adapter (Mappers DTO↔Domain).

---

## 🛡️ Error Handling Strategy
Se debe utilizar la clase `Result` nativa de Kotlin para propagar errores desde el Repositorio hasta el ViewModel.

```kotlin
// Repositorio
override suspend fun getData(): Result<Data> = runCatching {
    remoteSource.fetch()
}

// ViewModel
viewModelScope.launch {
    useCase().onSuccess { /* Update State */ }.onFailure { /* Send Effect */ }
}
```

---
**Tip**: Mantén los UseCases pequeños. Si un UseCase tiene más de 100 líneas, probablemente estés mezclando lógica de negocio con lógica de datos.
