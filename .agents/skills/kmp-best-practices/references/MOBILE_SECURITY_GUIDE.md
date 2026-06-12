# Mobile Security Guide for KMP (Android / iOS)

Toda implementación debe asumir que **el binario será decompilado y el tráfico interceptado**. El objetivo: aunque alguien haga ingeniería inversa del APK/IPA, no debe obtener claves, secretos ni acceso a datos sensibles.

> [!IMPORTANT]
> La seguridad no es una feature opcional ni un paso final: se aplica en cada capa desde el primer commit. Ante la duda, sigue el estándar **[OWASP MASVS](https://mas.owasp.org/MASVS/)**.

---

## 🔑 1. Secretos y Claves (Regla #1: nada en duro)

- ❌ **PROHIBIDO** hardcodear API keys, tokens, passwords o URLs sensibles en código Kotlin/Swift, recursos, `strings.xml` o `Info.plist`. Los strings del binario se extraen en segundos con `strings`/`jadx`.
- ✅ Define los secretos en `local.properties` (fuera del repo, en `.gitignore`) e inyéctalos con **BuildKonfig**:

```kotlin
// build.gradle.kts — lee de local.properties o variables de entorno (CI)
buildkonfig {
    defaultConfigs {
        buildConfigField(STRING, "API_KEY", localProperties.getProperty("API_KEY") ?: System.getenv("API_KEY") ?: "")
    }
}
```

- ⚠️ BuildKonfig **dificulta** pero no oculta: el valor sigue dentro del binario. Para secretos realmente críticos:
  - **Mejor opción**: que el secreto **nunca viaje en la app**. Muévelo a tu backend (proxy de API) y protege el endpoint con autenticación del usuario.
  - Si debe estar en el dispositivo, guárdalo cifrado tras el primer uso (ver §3) o recupéralo en runtime desde el backend tras autenticar.
- ✅ Revisa antes de cada commit que no se filtren secretos (`git diff` + herramientas como `gitleaks` en CI).

---

## 🌐 2. Red: TLS y SSL Pinning

- ✅ **Solo HTTPS/TLS**. Prohibido `http://` en cualquier entorno, incluido debug contra servicios reales.
- ✅ Aplica **Certificate/Public-Key Pinning** en el `HttpClient` de Ktor para mitigar ataques MitM (proxies tipo Charles/mitmproxy con CA instalada):

```kotlin
// androidMain — pinning vía OkHttp
val pinner = CertificatePinner.Builder()
    .add("api.midominio.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .add("api.midominio.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=") // backup pin
    .build()

HttpClient(OkHttp) { engine { config { certificatePinner(pinner) } } }
```

- **iOS (Darwin engine)**: implementa el pinning en `handleChallenge` validando el certificado del servidor, o usa una librería KMP de pinning (verifica `libs.versions.toml`).
- ✅ Incluye siempre **al menos un pin de respaldo** (certificado futuro) para no bloquear la app al rotar certificados.
- ❌ Nunca deshabilites la validación TLS (`trustAll`, `allowsArbitraryLoads`) ni en builds de debug que lleguen a testers.
- ✅ En Android, restringe el tráfico con `networkSecurityConfig` (`cleartextTrafficPermitted="false"`); en iOS no relajes **App Transport Security**.

---

## 💾 3. Almacenamiento Seguro en el Dispositivo

| Tipo de dato | Dónde guardarlo |
|--------------|-----------------|
| Tokens, credenciales, secretos | **Android Keystore** / **iOS Keychain** (vía `expect`/`actual` o librería KMP tipo `multiplatform-settings` con cifrado) |
| Preferencias no sensibles | DataStore (sin cifrar está bien) |
| Datos estructurados sensibles | Room + **SQLCipher** (o cifra los campos sensibles antes de insertar) |

```kotlin
// commonMain — contrato; cada plataforma usa su almacén seguro nativo
interface SecureStorage {
    suspend fun save(key: String, value: String)
    suspend fun read(key: String): String?
    suspend fun delete(key: String)
}
// androidMain → Keystore (AES/GCM) · iosMain → Keychain Services
```

### Reglas
- ❌ Nunca guardes tokens/credenciales en DataStore o Room **en texto plano**.
- ✅ Cifrado simétrico: **AES-256-GCM** con claves generadas y custodiadas por Keystore/Keychain (la clave nunca vive en el código).
- ✅ Marca los datos del Keychain como `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`; en Android usa claves con `setUserAuthenticationRequired` cuando aplique.
- ✅ Excluye datos sensibles de los backups (`android:allowBackup="false"` o reglas de backup; en iOS atributos `NSURLIsExcludedFromBackupKey`).
- ✅ Limpia los secretos al cerrar sesión.

---

## 🕵️ 4. Ofuscación y Endurecimiento del Binario

### Android (R8)
- ✅ Activa en todo build de release: `isMinifyEnabled = true` + `isShrinkResources = true`.
- ✅ Mantén las reglas en `proguard-rules.pro` mínimas: cada `-keep` innecesario es código sin ofuscar. Revisa el `mapping.txt` generado (y súbelo a tu crash reporter, nunca al repo público).
- ✅ Renombra/ofusca también los modelos de datos solo si no rompes la serialización (`kotlinx.serialization` necesita `-keep` específicos).

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

### iOS
- Los frameworks de Kotlin/Native exponen nombres: minimiza la API pública del módulo compartido (`internal` por defecto, `explicitApi()` en Gradle).
- Compila release con optimizaciones (`-Os`) y sin símbolos de debug en el binario distribuido (dSYM separado para crash reports).

---

## 🚫 5. Higiene de Código y Datos en Runtime

- ❌ **Logs**: prohibido loguear tokens, PII, cuerpos de request/response en release. Configura `Logging` de Ktor a `LogLevel.NONE` en release y usa un logger que se desactive por build type.
- ❌ No expongas datos sensibles en mensajes de error, analytics ni crash reports.
- ✅ Oculta contenido sensible en el app switcher (Android `FLAG_SECURE` en pantallas críticas; iOS blur/overlay en `sceneWillResignActive`).
- ✅ Deshabilita capturas/screen recording en pantallas de alta sensibilidad (`FLAG_SECURE`).
- ✅ Valida y sanitiza **deep links / universal links**: nunca confíes en parámetros externos para saltar autenticación o navegar a pantallas privilegiadas.
- ✅ WebViews: deshabilita JavaScript si no se necesita, nunca cargues URLs arbitrarias ni expongas bridges (`addJavascriptInterface`) sin validación.
- ✅ `android:exported="false"` por defecto en Activities/Services/Receivers que no necesiten ser públicos.

---

## 🛡️ 6. Defensas Adicionales (según criticidad de la app)

Para apps con datos de alto valor (fintech, salud), considera además:

- **Root/Jailbreak detection**: degradar funcionalidad o alertar en dispositivos comprometidos (librerías como RootBeer en Android; chequeos de Keychain/paths en iOS). Es disuasión, no garantía.
- **Detección de debugger/hooking** (Frida, Xposed): chequeos en runtime para apps críticas.
- **Integridad de la app**: **Play Integrity API** (Android) y **App Attest / DeviceCheck** (iOS) para que el backend verifique que habla con una app legítima no modificada.
- **RASP / App shielding** comercial si el negocio lo justifica.

---

## ✅ Security Checklist (antes de cada release)

- [ ] ¿Cero secretos hardcodeados? (busca `apiKey`, `password`, `secret`, `token` en el código y recursos)
- [ ] ¿SSL Pinning activo y con pin de respaldo en todos los `HttpClient`?
- [ ] ¿Tokens y credenciales en Keystore/Keychain, nunca en texto plano?
- [ ] ¿R8/ofuscación habilitada en release y reglas `-keep` justificadas?
- [ ] ¿Logging de red y logs de debug deshabilitados en release?
- [ ] ¿Deep links y WebViews validados?
- [ ] ¿Backups excluyen datos sensibles?
- [ ] ¿Se decompiló el release (jadx/Hopper) para verificar que no se lee nada sensible?

---
**Referencia maestra**: [OWASP Mobile Application Security (MAS)](https://mas.owasp.org/) — MASVS para requisitos y MASTG para técnicas de verificación.
