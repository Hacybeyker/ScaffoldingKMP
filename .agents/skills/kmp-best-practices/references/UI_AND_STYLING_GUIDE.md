# UI & Styling Guide for Compose Multiplatform

Este documento detalla el sistema de diseño basado en **Material Design 3 (M3)** y los patrones de Compose recomendados.

## 🎨 Material Design 3

### Theming Dinámico & Dark Mode
El `AppTheme` debe soportar el sistema de colores dinámicos en Android 12+ y proveer una paleta armónica para iOS.

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

### Componentes M3 Críticos
- `Scaffold`: Provee el esqueleto base (topBar, snackbarHost).
- `Surface`: Fondo primario con elevación tonal.
- `OutlinedTextField`: Campo de texto estándar con estados de error.

---

## 🏗️ Compose Best Practices

### State Hoisting
El estado debe residir en el ancestro común más bajo. Las pantallas son "Stateful" (gestionan el ViewModel), pero los componentes deben ser "Stateless".

### Side Effects Management
- `LaunchedEffect`: Disparadores únicos (ej. navegación tras éxito).
- `rememberCoroutineScope`: Para llamadas desde clics del usuario.
- `collectAsStateWithLifecycle`: Recomendado para evitar consumos innecesarios en background (Android).

### Optimización (Lazy Lists)
- **Key**: Usa siempre el parámetro `key` en `items` para evitar recomposiciones costosas.
- **DerivedStateOf**: Úsalo para cálculos pesados basados en estados que cambian rápido (ej. scroll position).

---

## 🖼️ Gestión de Imágenes (Coil3)
Usa `AsyncImage` para cargar imágenes de red de forma eficiente en KMP:
- **Placeholders**: Usa recursos locales mientras carga.
- **Crossfade**: Recomendado para una transición suave.

---

## 📱 Accesibilidad & UI Nativa
- **Content Description**: Obligatorio en iconos que no tengan label textual.
- **Safe Areas**: Respeta los insets del sistema (especialmente en iOS/Dynamic Island) usando `Modifier.safeDrawingPadding()`.
- **Toque**: Mínimo **48dp** para cualquier elemento clickable.
