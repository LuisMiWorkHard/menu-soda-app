# Arquitectura de referencia — Kotlin Multiplatform + Compose Multiplatform

> **Cómo usar este archivo:** Es la guía de arquitectura que **Claude debe seguir** al escribir
> código en este proyecto. Define la estructura de capas (Clean Architecture + **MVI**), los
> *source sets* de KMP, las convenciones de nombres y **dónde colocar cada tipo de archivo**.
> Cópialo como `CLAUDE.md` en la raíz del nuevo proyecto KMP (o impórtalo desde tu `CLAUDE.md`).
>
> Plataformas objetivo: **Android + iOS**.
> Capas `data`/`domain` adaptadas desde un proyecto Android (Clean Architecture) probado en
> producción. **La capa de presentación usa MVI** (Model-View-Intent), no MVVM clásico: estado
> único inmutable por pantalla, eventos del usuario y efectos de una sola vez (ver §5).

---

## 1. Stack tecnológico (equivalentes KMP)

| Necesidad            | KMP / CMP (usar esto)                                                        |
|----------------------|-----------------------------------------------------------------------------|
| UI                   | **Compose Multiplatform** (`org.jetbrains.compose`)                         |
| Inyección dependencias | **Koin** (`koin-core` en common, `koin-android` en androidMain)            |
| Red / HTTP           | **Ktor 3** (`ktor-client-core` común; engine `okhttp`/`android` + `darwin`)|
| Serialización        | kotlinx.serialization         | **kotlinx.serialization** (común, sin cambios)                              |
| Patrón de presentación | **MVI** — State inmutable + Event + Effect (ver §5)                         |
| ViewModel / Store    | **`androidx.lifecycle.ViewModel`** (KMP) como *store* MVI + `koinViewModel()` |
| Navegación           | **Navigation Compose Multiplatform** (`org.jetbrains.androidx.navigation`)  |
| Estado de pantalla   | **`StateFlow<UiState>`** — un único `data class` inmutable por pantalla     |
| Eventos de una vez   | **`Effect`** vía `Channel` → `Flow` (navegación, toasts, snackbars)         |
| Almacenamiento seguro| **`expect/actual`**: Android = DataStore+Tink · iOS = Keychain             |
| Imágenes             | **Coil 3** (ya es multiplataforma; usar en `commonMain`)                    |
| Logging              | **Kermit** (`co.touchlab:kermit`) — NUNCA `android.util.Log` en common      |
| Flags de build       | **`expect fun isDebug(): Boolean`** o plugin BuildKonfig                    |
| Animaciones          | Lottie no es KMP → usar `compottie` o animaciones Compose nativas          |

**Regla de oro:** todo el código de negocio, red, estado y UI vive en `commonMain`.
Solo lo que el SDK de la plataforma exige (Keychain, DataStore, engine HTTP, deviceId…) va en
`androidMain` / `iosMain` detrás de una abstracción `expect/actual`.
**Recomendar:** si existe alguna mejor opción que las librerías sugeridas en la lista mencionalas y explica 
por que seria mejor usarlas.
---

## 2. Estructura de módulos Gradle

Proyecto generado con el wizard de Compose Multiplatform (Kotlin Multiplatform plugin):

```
nuevo-proyecto/
├── composeApp/                 ← módulo KMP compartido + entrypoint Android/Desktop
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/         ← 95% del código vive aquí
│       ├── androidMain/        ← implementaciones `actual` Android
│       └── iosMain/            ← implementaciones `actual` iOS
├── iosApp/                     ← proyecto Xcode (SwiftUI host) — generado, casi no se toca
├── gradle/libs.versions.toml   ← catálogo de versiones (igual que el proyecto base)
└── settings.gradle.kts
```

> Si prefieres separar UI de lógica, usa `shared/` (KMP puro) + `composeApp/` (UI). Para empezar,
> un único módulo `composeApp` es suficiente y es la convención por defecto del wizard.

### 2.1 ¿Modularizar? Cuándo y cómo

**Sí, el proyecto se puede modularizar.** Pero la recomendación por defecto es:
**empezar monomódulo (`composeApp`) y extraer módulos cuando haya una razón real** (tiempos de
build altos, varios devs pisándose, o un límite de dominio muy claro y reutilizable). La estructura
de paquetes de §3 ya está alineada con esto: extraer un feature a su propio módulo es **mecánico**
(mover paquetes), no una reescritura.

#### Estrategia recomendada: por *feature* + módulos `core` (vertical slicing)

No modularices por capa (`:data`, `:domain`, `:presentation`) — genera acoplamiento y módulos
gigantes. Modulariza por **feature**, con módulos `core` transversales (enfoque *Now in Android*):

```
:composeApp                 ← ensambla todo, NavHost raíz, startKoin, produce el framework iOS
:core:common                ← utils, dispatchers, logger (Kermit), Result/State
:core:designsystem          ← ui/theme + Composables reutilizables (common/components)
:core:network               ← HttpClients Ktor (Public/Auth), ApiException, interceptor 401
:core:storage               ← SecureStorageProvider (expect/actual) + DataStore/Keychain
:core:domain                ← modelos de dominio + DTOs + interfaces de repo compartidas
:feature:auth               ← login / recuperar contraseña
:feature:menu               ← menú, entradas, platos, estilo
:feature:perfil             ← perfil, información personal, cambiar contraseña
```

Cada `:feature:*` y `:core:*` es un **módulo KMP completo** (con `commonMain`/`androidMain`/`iosMain`)
y, dentro, mantiene la misma estructura `data`/`domain`/`presentation` de §3.

#### Cómo mapea a la estructura actual (monomódulo → multi-módulo)

| Hoy (paquete en `composeApp`)            | Mañana (módulo)            |
|------------------------------------------|----------------------------|
| `presentation/features/menu/…`           | `:feature:menu`            |
| `presentation/features/login` + recuperar| `:feature:auth`            |
| `di/NetworkModule.kt` + `data/.../remote`| `:core:network`            |
| `ui/theme/` + `presentation/common/components` | `:core:designsystem` |
| `presentation/common/utils` (helpers puros) | `:core:common`          |
| `data/datasource/local` (SecureStorage)  | `:core:storage`            |
| `domain/model` + DTOs compartidos        | `:core:domain`             |

#### Particularidades de KMP (vs. multi-módulo Android puro)

1. **Targets por módulo:** cada módulo declara `androidTarget()` + `iosX64()/iosArm64()/iosSimulatorArm64()`.
   Para no repetirlo, usa **convention plugins** en `build-logic/` (composite build) —
   p. ej. `convention.kmp.library` y `convention.kmp.feature`. Es la mejor opción frente a copiar
   config en cada `build.gradle.kts`; centraliza targets, Compose, serialization y Koin.
   *(Alternativa emergente: definir módulos con **Amper** — YAML declarativo de JetBrains — si prefieres
   menos boilerplate que Gradle Kotlin DSL; aún en evolución, evaluar antes de adoptar.)*
2. **Un solo framework iOS:** solo `:composeApp` exporta el framework a `iosApp`. Los `:feature:*`/`:core:*`
   son dependencias normales que se enlazan ahí. No exportes framework desde cada módulo.
3. **Koin distribuido:** cada módulo expone su propio `val xModule = module { … }`; `:composeApp`
   los agrega: `startKoin { modules(coreNetworkModule, featureMenuModule, …) }`.
4. **Navegación desacoplada:** cada feature expone su subgrafo (`fun NavGraphBuilder.menuGraph(...)`)
   o sus rutas, y el `NavHost` raíz en `:composeApp` los compone. **MVI encaja perfecto:** como la
   navegación se emite como `Effect`, un feature nunca conoce a otro — emite el efecto y `:composeApp`
   decide el destino (callbacks `onNavigateX`).
5. **Sin ciclos:** `:feature:*` → dependen de `:core:*`, **nunca** de otro `:feature:*`. Lo que dos
   features compartan, sube a `:core:*`. Usa `api` solo para lo que deba ser transitivo; el resto `implementation`.

#### Recomendación pragmática para este proyecto

Mantén **un único `composeApp`** ahora con la estructura de §3. Cuando el build empiece a molestar o
entren más devs: extrae **primero los `:core:*`** (network, designsystem, common), y **después** los
`:feature:*`. No necesitas decidirlo hoy: la organización por paquetes ya deja el camino preparado.

---

## 3. Estructura de paquetes (Clean Architecture) — idéntica al proyecto base

Raíz de paquete: `com.fullwar.<appname>` dentro de **`commonMain/kotlin/`**.

```
commonMain/kotlin/com/fullwar/<appname>/
├── App.kt                      ← Composable raíz (antes MainActivity setContent)
├── di/                         ← módulos de Koin
│   ├── AppModule.kt            ← viewModels, repositorios, services
│   ├── NetworkModule.kt        ← HttpClients (Public / Auth)
│   └── Constants.kt            ← BASE_URL y constantes globales
├── data/                       ← detalles de implementación (capa externa)
│   ├── datasource/
│   │   ├── local/              ← interfaces + abstracciones de almacenamiento local
│   │   └── remote/             ← *Service Ktor (uno por recurso de API)
│   ├── model/                  ← DTOs (@Serializable, sufijo Dto) + ApiException
│   │   └── mapper/             ← mappers DTO ↔ modelo de dominio
│   ├── repository/             ← *RepositoryImpl (implementan interfaces de dominio)
│   └── util/                   ← AuthEventBus, ImageCompressor, helpers de data
├── domain/                     ← abstracciones puras (sin dependencias de framework)
│   ├── model/                  ← modelos/enums de dominio (TipoDocumento, etc.)
│   ├── repository/             ← interfaces de repositorio (prefijo I)
│   └── usecase/                ← (opcional) casos de uso; ver §7
├── presentation/               ← UI + Stores MVI
│   ├── features/<feature>/     ← <Feature>Contract.kt + <Feature>ViewModel.kt + <Feature>Screen.kt
│   ├── common/
│   │   ├── mvi/                ← (opcional) UiState/UiEvent/UiEffect markers + BaseViewModel
│   │   ├── components/         ← Composables reutilizables
│   │   └── utils/              ← FuzzyMatcher, StringUtils, State.kt (helper opcional)…
│   └── navigation/             ← AppScreens.kt (rutas) + SetupNavigation.kt (NavHost)
└── ui/theme/                   ← Color.kt, Theme.kt, Type.kt, Dimens.kt

androidMain/kotlin/com/fullwar/<appname>/
├── MainActivity.kt             ← host Android, llama a App()
├── MainApplication.kt          ← startKoin(), config de Coil
└── ...                         ← implementaciones `actual` (ver §6)

iosMain/kotlin/com/fullwar/<appname>/
├── MainViewController.kt       ← entrypoint iOS (ComposeUIViewController { App() })
└── ...                         ← implementaciones `actual` (ver §6)
```

---

## 4. Dónde colocar cada cosa (tabla de decisión)

| Voy a crear…                                   | Carpeta / source set                                  | Nombre del archivo            |
|------------------------------------------------|-------------------------------------------------------|-------------------------------|
| Una pantalla nueva                             | `commonMain/.../presentation/features/<feature>/`     | `<Feature>Screen.kt`          |
| El contrato MVI (State + Event + Effect)       | `commonMain/.../presentation/features/<feature>/`     | `<Feature>Contract.kt`        |
| El store/ViewModel MVI de esa pantalla         | `commonMain/.../presentation/features/<feature>/`     | `<Feature>ViewModel.kt`       |
| Un Composable reutilizable (botón, dialog…)    | `commonMain/.../presentation/common/components/`      | `<Nombre>.kt`                 |
| Un helper de UI puro (formato, fuzzy match)    | `commonMain/.../presentation/common/utils/`           | `<Nombre>.kt`                 |
| Una ruta de navegación                         | `commonMain/.../presentation/navigation/AppScreens.kt`| (añadir `object` a la sealed) |
| Un DTO de request/response                     | `commonMain/.../data/model/`                          | `<Nombre><Tipo>Dto.kt`        |
| Un servicio HTTP de un recurso de API          | `commonMain/.../data/datasource/remote/`              | `<Recurso>Service.kt`         |
| Una interfaz de repositorio                    | `commonMain/.../domain/repository/`                   | `I<Recurso>Repository.kt`     |
| La implementación de ese repositorio           | `commonMain/.../data/repository/`                     | `<Recurso>RepositoryImpl.kt`  |
| Un modelo/enum de dominio                      | `commonMain/.../domain/model/`                        | `<Nombre>.kt`                 |
| Un mapper DTO ↔ dominio                         | `commonMain/.../data/model/mapper/`                   | `<Nombre>Mapper.kt`           |
| Registrar VM/repo/service en DI                | `commonMain/.../di/AppModule.kt`                       | (añadir línea)                |
| Una abstracción de plataforma (`expect`)       | `commonMain/.../data/datasource/local/`               | `<Nombre>.kt` (`expect`)      |
| Su implementación Android (`actual`)           | `androidMain/.../data/datasource/local/`              | `<Nombre>.android.kt`         |
| Su implementación iOS (`actual`)               | `iosMain/.../data/datasource/local/`                  | `<Nombre>.ios.kt`             |

**Heurística de capa:**
- ¿Es Compose / pantalla / estado de UI? → `presentation`
- ¿Habla con la red o el disco? → `data`
- ¿Es una abstracción o regla de negocio sin framework? → `domain`
- ¿Necesita una API específica de Android/iOS? → `expect` en common, `actual` por plataforma

---

## 5. Convenciones por capa (con ejemplos KMP)

### 5.1 `domain/` — abstracciones puras

Interfaces de repositorio con prefijo `I`, todas las operaciones `suspend`:

```kotlin
// domain/repository/IEntradaRepository.kt
interface IEntradaRepository {
    suspend fun getEntradas(): List<EntradaResponseDto>
    suspend fun searchEntradas(query: String): List<EntradaResponseDto>
    suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto
    suspend fun deleteEntrada(id: Int)
}
```

> El dominio no importa nada de Ktor, Compose ni de plataforma. Solo Kotlin/coroutines.

### 5.2 `data/model/` — DTOs

`@Serializable`, sufijo `Dto`, `@SerialName` explícito, valores por defecto para campos opcionales:

```kotlin
// data/model/EntradaResponseDto.kt
@Serializable
data class EntradaResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String = "",
    @SerialName("imagenId") val imagenId: Int? = null,
)
```

Error tipado compartido (sin cambios respecto al proyecto base):

```kotlin
// data/model/ApiException.kt
class ApiException(
    val statusCode: Int,
    val errorDetail: String?,
    val errorCode: String?,
    val validationErrors: Map<String, List<String>>? = null,
) : Exception(errorDetail ?: "Error desconocido del servidor (código $statusCode)")
```

### 5.3 `data/datasource/remote/` — Services Ktor

Uno por recurso de API. Recibe `HttpClient`, devuelve DTOs, loguea con **Kermit** (no `android.util.Log`):

```kotlin
// data/datasource/remote/EntradaService.kt
class EntradaService(private val httpClient: HttpClient) {
    private val log = Logger.withTag("EntradaService")  // co.touchlab.kermit.Logger

    suspend fun getEntradas(filter: String? = null): List<EntradaResponseDto> {
        val response = httpClient.get("api/entrada") {
            filter?.takeIf { it.isNotBlank() }?.let { parameter("filter", it) }
        }
        log.d { "getEntradas() - Status: ${response.status.value}" }
        return response.body()
    }

    suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto =
        httpClient.post("api/entrada") { setBody(request) }.body()
}
```

### 5.4 `data/repository/` — Implementaciones

Sufijo `Impl`, implementan la interfaz de dominio, orquestan services y caché.
**Importante:** en common NO existe `Dispatchers.IO`. Usar `Dispatchers.Default` o inyectar el
dispatcher; para I/O de red Ktor ya suspende correctamente, así que basta `Dispatchers.Default`
para trabajo CPU (filtros, mapeos):

```kotlin
// data/repository/EntradaRepositoryImpl.kt
class EntradaRepositoryImpl(
    private val entradaService: EntradaService,
) : IEntradaRepository {

    private var cachedEntradas: List<EntradaResponseDto>? = null

    override suspend fun getEntradas(): List<EntradaResponseDto> =
        entradaService.getEntradas().also { cachedEntradas = it }

    override suspend fun searchEntradas(query: String): List<EntradaResponseDto> =
        withContext(Dispatchers.Default) {  // ← NO Dispatchers.IO en commonMain
            entradaService.getEntradas(filter = query)
        }

    override suspend fun deleteEntrada(id: Int) {
        entradaService.deleteEntrada(id).also { cachedEntradas = null }
    }
}
```

### 5.5 Capa de presentación = MVI

Cada pantalla se modela con **tres piezas** (Model-View-Intent):

| Pieza      | Qué es                                                        | Dirección         |
|------------|--------------------------------------------------------------|-------------------|
| **State**  | `data class` inmutable con TODO lo que la UI necesita pintar  | Store → UI (`StateFlow`) |
| **Event**  | `sealed interface` con las intenciones del usuario           | UI → Store (`onEvent`)   |
| **Effect** | `sealed interface` de acciones de una sola vez (navegar, toast) | Store → UI (`Flow`/`Channel`) |

Flujo unidireccional: **UI emite `Event` → Store reduce y produce nuevo `State` → UI se recompone**;
los efectos puntuales (no parte del estado) viajan por un canal de `Effect`.

> El antiguo `State<T>` (Initial/Loading/Success/Error) ya **no** es el estado de pantalla. En MVI
> el estado de carga/error son **campos** del `UiState` (`isLoading`, `errorMessage`). Puedes
> conservar `State<T>` en `common/utils/` como helper para representar un sub-recurso asíncrono
> concreto dentro del `UiState`, pero no es obligatorio.

#### Contrato — `<Feature>Contract.kt`

```kotlin
// presentation/features/menu/entrada/seleccion/SeleccionEntradasContract.kt

// 1) ESTADO inmutable y único de la pantalla
data class SeleccionEntradasState(
    val isLoading: Boolean = false,
    val query: String = "",
    val entradas: List<EntradaResponseDto> = emptyList(),
    val searchResults: List<EntradaResponseDto> = emptyList(),
    val errorMessage: String? = null,
)

// 2) EVENTOS (intenciones del usuario)
sealed interface SeleccionEntradasEvent {
    data object Load : SeleccionEntradasEvent
    data class Search(val query: String) : SeleccionEntradasEvent
    data class Delete(val id: Int) : SeleccionEntradasEvent
    data class EntradaClicked(val id: Int) : SeleccionEntradasEvent
}

// 3) EFECTOS (acciones de una sola vez)
sealed interface SeleccionEntradasEffect {
    data class NavigateToDetail(val id: Int) : SeleccionEntradasEffect
    data class ShowError(val message: String) : SeleccionEntradasEffect
}
```

### 5.6 Store MVI — `<Feature>ViewModel.kt`

`androidx.lifecycle.ViewModel` (KMP) como *store*. Expone `StateFlow<State>` + un `Flow<Effect>`,
y un único punto de entrada `onEvent(event)` que despacha (reduce) cada intención. El estado se
actualiza siempre con `_state.update { it.copy(...) }` (inmutable):

```kotlin
// presentation/features/menu/entrada/seleccion/SeleccionEntradasViewModel.kt
class SeleccionEntradasViewModel(
    private val entradaRepository: IEntradaRepository,
) : ViewModel() {

    private val log = Logger.withTag("SeleccionEntradasViewModel")

    private val _state = MutableStateFlow(SeleccionEntradasState())
    val state: StateFlow<SeleccionEntradasState> = _state.asStateFlow()

    // Canal para efectos de una sola vez (no sobreviven a recomposición/rotación)
    private val _effect = Channel<SeleccionEntradasEffect>(Channel.BUFFERED)
    val effect: Flow<SeleccionEntradasEffect> = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    // ÚNICO punto de entrada de intenciones del usuario
    fun onEvent(event: SeleccionEntradasEvent) {
        when (event) {
            is SeleccionEntradasEvent.Load          -> load()
            is SeleccionEntradasEvent.Search         -> search(event.query)
            is SeleccionEntradasEvent.Delete         -> delete(event.id)
            is SeleccionEntradasEvent.EntradaClicked -> emit(SeleccionEntradasEffect.NavigateToDetail(event.id))
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val entradas = entradaRepository.getEntradas()
                _state.update { it.copy(isLoading = false, entradas = entradas, searchResults = entradas) }
            } catch (e: Exception) {
                log.e(e) { "Error cargando entradas" }
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                emit(SeleccionEntradasEffect.ShowError(e.message ?: "Error cargando entradas"))
            }
        }
    }

    private fun search(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val results = entradaRepository.searchEntradas(query)
                _state.update { it.copy(searchResults = results) }
            } catch (e: Exception) {
                log.e(e) { "Error buscando entradas" }
            }
        }
    }

    private fun delete(id: Int) {
        viewModelScope.launch {
            try {
                entradaRepository.deleteEntrada(id)
                load()
            } catch (e: Exception) {
                log.e(e) { "Error eliminando entrada" }
                emit(SeleccionEntradasEffect.ShowError("No se pudo eliminar"))
            }
        }
    }

    private fun emit(effect: SeleccionEntradasEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
```

### 5.6.1 Cableado de la `Screen` — colectar State y Effect

La pantalla se divide en dos: el **contenedor** (obtiene el store, colecta `state`/`effect`) y el
**contenido puro** (recibe `state` + `onEvent`, sin lógica). El contenido puro es fácil de previsualizar/testear:

```kotlin
// presentation/features/menu/entrada/seleccion/SeleccionEntradasScreen.kt
@Composable
fun SeleccionEntradasScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: SeleccionEntradasViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    // Efectos de una sola vez
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SeleccionEntradasEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
                is SeleccionEntradasEffect.ShowError        -> snackbar.showSnackbar(effect.message)
            }
        }
    }

    // Carga inicial: una intención más
    LaunchedEffect(Unit) { viewModel.onEvent(SeleccionEntradasEvent.Load) }

    SeleccionEntradasContent(
        state = state,
        snackbar = snackbar,
        onEvent = viewModel::onEvent,   // la UI solo emite eventos
    )
}

@Composable
private fun SeleccionEntradasContent(
    state: SeleccionEntradasState,
    snackbar: SnackbarHostState,
    onEvent: (SeleccionEntradasEvent) -> Unit,
) {
    // pinta según `state`; cada interacción llama onEvent(SeleccionEntradasEvent.X(...))
}
```

> El store se obtiene con `koinViewModel()` (de `koin-compose-viewmodel`). `collectAsStateWithLifecycle`
> viene de `androidx.lifecycle:lifecycle-runtime-compose` (artefacto KMP).

### 5.6.2 (Opcional) Base MVI reutilizable — `presentation/common/mvi/`

Si quieres uniformar todos los stores, define marcadores y una base:

```kotlin
// presentation/common/mvi/Mvi.kt
interface UiState
interface UiEvent
interface UiEffect

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(initial: S) : ViewModel() {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<F>(Channel.BUFFERED)
    val effect: Flow<F> = _effect.receiveAsFlow()

    protected fun setState(reducer: S.() -> S) = _state.update(reducer)
    protected fun sendEffect(effect: F) { viewModelScope.launch { _effect.send(effect) } }

    abstract fun onEvent(event: E)
}
```

Luego cada feature: `class XViewModel(...) : BaseViewModel<XState, XEvent, XEffect>(XState())`.

### 5.7 `presentation/navigation/` — Navegación

`AppScreens` como `sealed class` de rutas con builders de ruta; `SetupNavigation(startDestination)`
con `NavHost`. La API de Navigation Compose Multiplatform es la misma que la de Android.

**En MVI la navegación es un `Effect`,** no se llama al `navController` desde el store. La `Screen`
recibe callbacks `onNavigateX` (cableados en `SetupNavigation`) y los invoca al colectar el efecto
correspondiente (ver §5.6.1). El store nunca conoce al `navController`.

```kotlin
// presentation/navigation/AppScreens.kt
sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object MenuScreen : AppScreens("menu_screen?menuId={menuId}") {
        fun withId(menuId: Int) = "menu_screen?menuId=$menuId"
    }
}
```

---

## 6. Patrón `expect`/`actual` para código de plataforma

Toda dependencia de SDK Android/iOS se abstrae. El proyecto base **ya dejó documentado** este
patrón en `SecureStorageProvider`. Reglas:

1. La **interfaz/contrato** vive en `commonMain` (puede ser una `interface` normal inyectada por
   Koin, o una `expect class`).
2. Las **implementaciones** viven en `androidMain` y `iosMain`.
3. Se conectan vía Koin: cada `actual`/impl se registra en el módulo de plataforma (ver §8).

### Ejemplo: almacenamiento seguro

```kotlin
// commonMain/.../data/datasource/local/SecureStorageProvider.kt
interface SecureStorageProvider {
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
    suspend fun remove(key: String)
    suspend fun clear()
}
```

```kotlin
// androidMain/.../data/repository/SecureDataStoreImpl.kt
// Implementación con DataStore + Tink + Android Keystore (igual que el proyecto base)
class SecureDataStoreImpl(private val context: Context) : SecureStorageProvider { /* ... */ }
```

```kotlin
// iosMain/.../data/repository/SecureKeychainImpl.kt
// Implementación con Keychain Services (Security framework)
class SecureKeychainImpl : SecureStorageProvider { /* ... */ }
```

### Otras abstracciones que necesitarán `expect/actual`
| Concepto                  | Android (`actual`)                          | iOS (`actual`)                       |
|---------------------------|---------------------------------------------|--------------------------------------|
| Almacenamiento seguro     | DataStore + Tink                            | Keychain                             |
| `deviceId` (header)       | `Settings.Secure.ANDROID_ID`                | `UIDevice.identifierForVendor`       |
| Engine HTTP de Ktor       | `OkHttp` / `Android`                        | `Darwin`                             |
| `isDebug()`               | `BuildConfig.DEBUG`                         | flag de compilación / `Platform.isDebugBinary` |
| Ubicación (si aplica)     | Play Services Location                      | CoreLocation                         |

---

## 7. Casos de uso (`domain/usecase/`)

En el proyecto base esta carpeta existe pero está **vacía**: el store llama directamente a los
repositorios desde `onEvent`. **Mantén esa convención por defecto** (store → repositorio) para no
añadir ceremonia innecesaria.

Crea un caso de uso solo cuando una operación:
- combine varios repositorios, **o**
- contenga lógica de negocio no trivial reutilizada por varios stores.

En ese caso: `domain/usecase/<Verbo><Entidad>UseCase.kt` con un único `operator fun invoke(...)`.

---

## 8. Inyección de dependencias (Koin)

`commonMain/di/AppModule.kt` declara ViewModels, repositorios y services (todo lo común).
Los bindings de plataforma (`actual`) se declaran en un módulo por plataforma.

```kotlin
// commonMain/di/AppModule.kt
val appModule = module {
    // ViewModels
    viewModelOf(::SeleccionEntradasViewModel)

    // Services (usan el AuthClient con Bearer token)
    single { EntradaService(get(named("AuthClient"))) }

    // Repositorios: bind impl → interfaz de dominio
    singleOf(::EntradaRepositoryImpl) bind IEntradaRepository::class
}
```

```kotlin
// androidMain/di/PlatformModule.android.kt
actual val platformModule = module {
    single<SecureStorageProvider> { SecureDataStoreImpl(androidContext()) }
}
// iosMain/di/PlatformModule.ios.kt
actual val platformModule = module {
    single<SecureStorageProvider> { SecureKeychainImpl() }
}
```

`startKoin { modules(appModule, networkModule, platformModule) }` se llama en
`MainApplication` (Android) y en el entrypoint de iOS (`MainViewController`).

**Convenciones Koin (del proyecto base, mantener):**
- `viewModelOf(::X)` para ViewModels.
- `singleOf(::Impl) bind IInterface::class` para repositorios.
- Qualifiers `named("PublicClient")` / `named("AuthClient")` para los dos HttpClients.

---

## 9. Red (`di/NetworkModule.kt`)

Dos `HttpClient` con qualifier, igual que el proyecto base:
- **`PublicClient`** → login / refresh / logout (sin Bearer).
- **`AuthClient`** → inyecta `Authorization: Bearer` y hace **refresh automático en 401** con un
  `Mutex` (interceptor `HttpSend`).

Diferencia KMP: el **engine** se inyecta por plataforma (`expect fun httpClientEngine(): HttpClientEngineFactory`),
mientras que la configuración (Logging, ContentNegotiation, `HttpResponseValidator` que lanza
`ApiException`, `defaultRequest` con `BASE_URL` y header `DeviceId`) vive en `commonMain` sin cambios.

```kotlin
// commonMain — engine como expect
expect fun httpClientEngine(): HttpClientEngineFactory<*>
// androidMain → OkHttp ;  iosMain → Darwin
```

---

## 10. Checklist al añadir una *feature* nueva

1. **DTOs** en `data/model/` (`@Serializable`, sufijo `Dto`).
2. **Service** Ktor en `data/datasource/remote/` (un método por endpoint).
3. **Interfaz** `I<Recurso>Repository` en `domain/repository/`.
4. **Impl** `<Recurso>RepositoryImpl` en `data/repository/`.
5. **Registrar** service + repo (`bind` a la interfaz) en `di/AppModule.kt`.
6. **Contrato MVI** `<Feature>Contract.kt`: `State` (data class) + `Event` (sealed) + `Effect` (sealed).
7. **Store** `<Feature>ViewModel.kt`: `StateFlow<State>` + `Flow<Effect>` + `onEvent(event)` con `_state.update`.
8. **Screen** en la misma carpeta: contenedor (colecta `state`/`effect`, `koinViewModel()`) + contenido puro (`state` + `onEvent`).
9. **Registrar** el store con `viewModelOf(::<Feature>ViewModel)` en `di/AppModule.kt`.
10. **Ruta** en `AppScreens` + `composable(...)` en `SetupNavigation`; pasar callbacks `onNavigateX` que el `Effect` dispara.
11. ¿Necesita SDK de plataforma? → abstracción en `commonMain` + `actual` en android/ios + binding en `platformModule`.

---

## 11. Reglas que Claude NO debe romper

- ❌ Nunca `android.util.Log`, `Dispatchers.IO`, `Context`, `BuildConfig` ni nada de `java.*` en
  `commonMain`. Usa Kermit, `Dispatchers.Default`, y abstracciones `expect/actual`.
- ❌ El `domain/` no depende de Ktor, Compose ni de plataforma.
- ❌ Los Composables no llaman a services/repositorios directamente: siempre vía store (`onEvent`).
- ❌ El store/ViewModel NO conoce el `navController`: la navegación se emite como `Effect`.
- ❌ No expongas varios `mutableStateOf` desde el store ni `MutableStateFlow` público: un único
  `StateFlow<State>` inmutable, modificado solo con `_state.update { it.copy(...) }`.
- ✅ **MVI:** cada pantalla = `State` (data class inmutable) + `Event` (sealed) + `Effect` (sealed),
  con un único `onEvent(event)` como punto de entrada.
- ✅ El `Effect` es para acciones de una sola vez (navegar, toast, snackbar); el resto va en `State`.
- ✅ DTOs en `data` con sufijo `Dto`; interfaces de repo con prefijo `I`; impls con sufijo `Impl`.
- ✅ Un `Service` por recurso de API; un `RepositoryImpl` por interfaz de dominio.
- ✅ Código nuevo en `commonMain` por defecto; baja a `androidMain`/`iosMain` solo si el SDK lo exige.
