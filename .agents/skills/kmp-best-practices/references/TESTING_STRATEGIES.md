# Testing Strategies for KMP

Garantizamos la estabilidad del proyecto mediante tres niveles: tests unitarios compartidos (`commonTest`), tests de UI con Compose Test y **screenshot tests** para regresión visual.

## 🧪 Unit Testing (commonTest)

### Regla de Oro: Sin Mocks
En Kotlin Multiplatform, las librerías de Mock (como MockK) pueden causar problemas de compatibilidad nativa. **Usa Fakes** (implementaciones reales pero controladas) en la carpeta `fakes/`.

### Estructura GIVEN / WHEN / THEN
```kotlin
@Test
fun givenSuccess_whenLoadItems_thenStateHasData() = runTest {
    // GIVEN
    val fakeRepo = FakeRepository().apply { emit(data) }
    val viewModel = MyViewModel(fakeRepo)

    // WHEN
    viewModel.onEvent(Load)

    // THEN
    assertEquals(data, viewModel.state.value.items)
}
```

### Herramientas Imprescindibles
- **kotlin-test**: Aserciones básicas (`assertEquals`, `assertTrue`).
- **Turbine**: La mejor forma de testear `Flow` y `StateFlow`.
- **kotlinx-coroutines-test**: Control del tiempo (`runTest`, `advanceUntilIdle`).

---

## 📱 UI Testing (androidUnitTest)
Utilizamos **Compose Test** junto con **Robolectric** para ejecutar tests de UI de alto rendimiento sin emulador.

```kotlin
@Test
fun shouldShowTitle_whenContentIsLoaded() = runComposeUiTest {
    setContent { MyContent(state = Loaded) }
    onNodeWithText("Título").assertIsDisplayed()
}
```

---

## 📸 Screenshot Testing (Regresión Visual)

Cada pantalla relevante debe tener un screenshot test que la capture en sus estados clave (Loading, Success, Error, Empty). Verifica en `gradle/libs.versions.toml` qué herramienta usa el proyecto; las opciones estándar son:

- **Roborazzi** (recomendada): se ejecuta sobre Robolectric en JVM, sin emulador.
- **Paparazzi**: alternativa JVM si el proyecto no usa Robolectric.

### Reglas
- Los tests se escriben **siempre contra el Composable `Content`** (stateless) con estados fake, nunca contra `Screen`.
- Cubre light/dark mode y, si aplica, distintos tamaños de fuente/pantalla.
- Las imágenes de referencia (golden images) se versionan en el repo; regenera con la tarea de "record" de la herramienta (ej. `./gradlew recordRoborazziDebug`) y verifica con la de "verify" en CI.

```kotlin
@Test
fun homeContent_successState() {
    composeRule.setContent { AppTheme { HomeContent(state = fakeSuccessState) } }
    composeRule.onRoot().captureRoboImage()
}
```

---

## ✅ Cobertura Recomendada
1. **ViewModels**: 100% de la lógica de flujo de estados.
2. **UseCases**: Lógica de negocio crítica y validaciones.
3. **Mappers**: Transformaciones de datos (especialmente de API a Domain).
4. **Screenshot tests**: Estados clave de cada pantalla (Loading / Success / Error / Empty).
