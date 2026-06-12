# Platform Implementations for KMP

Guía para manejar el patrón `expect`/`actual`, persistencia nativa y networking.

## 🔗 Expect / Actual Pattern

### Cuándo Usarlo
Solo cuando una librería multiplataforma no cubra la necesidad. Mantén la lógica en `commonMain` y solo delega la implementación a la plataforma.

### Mejores Prácticas
- Prefiere `interface` sobre `expect class` para inyectar implementaciones vía Koin.
- Marca los `actual object` o `actual class` con `@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")`.

---

## 💾 Persistencia (Room KMP)

### Configuración de la Base de Datos
```kotlin
// commonMain
@Database(entities = [MyEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase()

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
```

### Inicialización Nativa
Cada plataforma debe proveer el `driver` y el path del archivo:
- **Android**: `setDriver(BundledSQLiteDriver())`.
- **iOS**: `setDriver(BundledSQLiteDriver())` usando la ruta de `NSHomeDirectory()`.

---

## ⚙️ Preferencias (DataStore KMP)

Usa **DataStore (Preferences)** para configuraciones simples clave-valor (tema, onboarding visto, flags de usuario). Room queda reservado para datos estructurados/relacionales.

```kotlin
// commonMain — factory común
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

internal const val DATA_STORE_FILE_NAME = "app.preferences_pb"
```

Cada plataforma provee el path (vía Koin):
- **Android**: `context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath`.
- **iOS**: directorio de documentos vía `NSFileManager` + `DATA_STORE_FILE_NAME`.

### Reglas
- Accede a DataStore **solo desde la capa Data** (un `LocalSource`), expuesto al Domain a través de un Repository.
- Expón lecturas como `Flow<T>` para reactividad de extremo a extremo.

---

## 🌐 Networking (Ktor Client)

### HttpClient Factory
Configura el cliente en `core/network/` inyectando el motor correspondiente (`OkHttp` para Android, `Darwin` para iOS).

```kotlin
fun createHttpClient(engine: HttpClientEngine) = HttpClient(engine) {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.ALL }
}
```

---

## 🔑 Gestión de Secretos (BuildKonfig)
Las claves de API se definen en `local.properties` (que nunca se sube al repo) y se inyectan en el código mediante el plugin **BuildKonfig**.

```kotlin
// Acceso seguro
val apiKey = BuildKonfig.API_KEY
```

---

## 📸 Permisos (Moko-Permissions)
Para el manejo de permisos multiplataforma, se utiliza la librería **Moko-Permissions**. 
- **Setup**: Inyectar el `PermissionsController` vía Koin.
- **Android**: Requiere pasar el `Activity` o `Fragment` al controlador.
- **iOS**: Requiere declarar los `UsageDescription` en el `Info.plist`.

---
**Nota**: Si necesitas acceso a archivos, usa **Okio**.
