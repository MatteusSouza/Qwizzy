# AGENTS.md

Guidance for AI agents working in this repository.

## Project identity

- Git repo folder: `Qwizzy`
- Gradle `rootProject.name` = `"AskCeny"` (`settings.gradle.kts`)
- App namespace / applicationId: `com.example.askceny`
- Theme name: `Theme.AskCeny`

These three names (`Qwizzy` / `AskCeny` / `askceny`) all refer to the same app and are intentionally inconsistent for historical reasons. Do NOT "fix" them unless explicitly asked — renaming the package touches Firebase config, manifest, resources, and all Kotlin imports.

## Stack

Kotlin Multiplatform (KMP) structure: `:composeApp` (Android) + `:shared` (common + platform-specific).

- Kotlin `2.3.20`, AGP `8.11.2`, Gradle `8.14.3` wrapper (use `./gradlew`)
- Java source/target: `VERSION_11`
- SDK versions in version catalog: `compileSdk = 35`, `minSdk = 24`, `targetSdk = 35`
- **Data layer**: Room for local caching + Ktor HTTP client (Supabase ready; Firebase being phased out)
- **UI**: Compose Multiplatform (commonMain shared across platforms), Material3
- Coil3 (`io.coil-kt.coil3`) for async images
- Navigation Compose for routing (Android via `:composeApp`)
- All versions/aliases live in `gradle/libs.versions.toml` — add new deps there, then reference via `libs.<alias>` in build files.

## Commands

From repo root. The wrapper script is already executable (`gradlew`); on Windows use `gradlew.bat`.

| Task | Command |
| --- | --- |
| Assemble debug APK (Android) | `./gradlew :composeApp:assembleDebug` |
| Install debug on device (Android) | `./gradlew :composeApp:installDebug` |
| Lint (Android) | `./gradlew :composeApp:lintDebug` |
| Clean | `./gradlew clean` |
| Run shared module tests | `./gradlew :shared:test` |
| KSP (Room compiler) codegen | Automatic with build; check `shared/build/generated/` |

Currently, Android app entrypoint is `:composeApp:MainActivity` with `AskCenyApplication` as the Android `Application`. iOS entrypoint is `iosApp/iosApp/iOSApp.swift`, which boots Koin and hosts the Compose bridge from `composeApp`.

## Data layer & secrets

- **Local storage**: Room database (`:shared/data/local/AppDatabase.kt`). Platform-specific builders in `androidMain` and `iosMain`.
- **Remote API**: Ktor HTTP client (`:shared/data/remote/HttpClientProvider.kt`). Prepared for Supabase; currently scaffolded.
- **Auth/Firestore**: Being phased out. Legacy Firebase classes may remain in `AuthRepositoryImpl` / `QuizRepositoryImpl` until full Ktor/Supabase migration.
- No secrets file needed yet — Ktor/Supabase setup is pending.

## Architecture

MVVM with a swappable repository layer. KMP structure isolates platform concerns via `expect`/`actual` (Room builders, HTTP client).

### Module structure

```
:composeApp (UI host)
  ├─ depends on :shared
  ├─ commonMain/
  │    ├─ App.kt                   (shared Compose app root)
  │    └─ presentation/
  │         ├─ composables/        (screens and reusable UI)
  │         ├─ theme/              (Compose theme)
  │         └─ viewmodels/         (AuthViewModel, QuizViewModel)
  │
  ├─ androidMain/
  │    ├─ AskCenyApplication.kt    (Android Application bootstrap)
  │    ├─ MainActivity.kt          (Android entry point)
  │    └─ presentation/            (Android-specific UI actuals if needed)
  │
  └─ iosMain/
       ├─ MainViewController.kt    (iOS Compose bridge)
       └─ presentation/            (iOS-specific UI actuals if needed)

:shared (KMP library)
  ├─ commonMain/
  │    ├─ data/
  │    │    ├─ repositories/       (AuthRepositoryImpl, QuizRepositoryImpl)
  │    │    ├─ local/
  │    │    │    ├─ AppDatabase.kt  (Room @Database)
  │    │    │    ├─ DatabaseBuilder.kt (expect fun declaration)
  │    │    │    ├─ dao/            (QuizDao, QuestionDao, UserDao)
  │    │    │    └─ entities/       (Room @Entity classes)
  │    │    ├─ remote/
  │    │    │    ├─ HttpClientProvider.kt (expect fun declaration)
  │    │    │    └─ api/            (AuthRemoteDataSource, QuizRemoteDataSource)
  │    │    └─ di/
  │    │         └─ RepositoryProvider.kt
  │    ├─ domain/                  (business logic, use cases if any)
  │
  ├─ androidMain/
  │    ├─ data/
  │    │    ├─ local/
  │    │    │    └─ DatabaseBuilder.android.kt (actual fun, uses Room RoomDatabase.Builder)
  │    │    └─ remote/
  │    │         └─ HttpClientProvider.android.kt (actual fun, uses Ktor OkHttp engine)
  │
  └─ iosMain/
       ├─ data/
       │    ├─ local/
       │    │    └─ DatabaseBuilder.ios.kt (actual fun, uses Room BundledSQLiteDriver)
       │    └─ remote/
       │         └─ HttpClientProvider.ios.kt (actual fun, uses Ktor Darwin engine)
```

### Control flow (Android)

```
MainActivity
  └─ RepositoryProvider.container
       ├─ AuthRepositoryImpl
       │    ├─ Remote: AuthRemoteDataSource (Ktor HttpClient)
       │    └─ Local: UserDao (Room)
       └─ QuizRepositoryImpl
            ├─ Remote: QuizRemoteDataSource (Ktor HttpClient)
            └─ Local: QuizDao, QuestionDao (Room)
  └─ AuthViewModel, QuizViewModel (built via ViewModelProvider.Factory + CreationExtras)
  └─ setContent { App(...) }
        └─ App renders AskCenyTheme { Scaffold { MainScreen(...) } }
              └─ MainScreen renders CustomTopBar (per-route switch) + MyNavHost
              └─ NavHost(startDestination = "QuizzesList" | "SignIn")
```

### Room database lifecycle

- **AppDatabase** is defined in `commonMain` with all `@Entity` and `@Dao` classes.
- **DatabaseBuilder** is an `expect fun` in `commonMain` that returns a `RoomDatabase.Builder<AppDatabase>`.
- Platform-specific `actual fun` implementations:
  - **Android** (`androidMain`): Uses `Room.databaseBuilder(context, AppDatabase::class.java, ...)`.
  - **iOS** (`iosMain`): Uses `Room.databaseBuilder("..." , AppDatabase::class.java, ...).setDriver(BundledSQLiteDriver())`.

### Ktor HTTP client

- **HttpClientProvider** is an `expect fun` in `commonMain` that returns a configured `HttpClient`.
- Platform-specific `actual fun` implementations:
  - **Android** (`androidMain`): Uses OkHttp engine.
  - **iOS** (`iosMain`): Uses Darwin (NSURLSession) engine.

### ViewModel contract

Both ViewModels use `ViewModelProvider.Factory` + `CreationExtras.Key` for DI (no Hilt/Koin):

```kotlin
ViewModelProvider.create(
    owner,
    factory = AuthViewModel.Factory,
    extras = MutableCreationExtras().apply {
        set(AuthViewModel.USER_REPOSITORY_KEY, authRepository)
    },
)[AuthViewModel::class]
```

If you add a ViewModel, mirror this pattern (`companion object { val X_KEY = object : CreationExtras.Key<T> {}; val Factory = viewModelFactory { initializer { ... } } }`). Do **not** introduce Hilt; nothing else in the project expects it.

Exposed state uses `MutableStateFlow` privately with a `StateFlow` public alias (`private val _x; val x: StateFlow<...> = _x`). Screens consume via `by viewModel.x.collectAsState()`.

### Quiz selection pattern

There is no per-destination argument passing. The currently-active quiz is stored in `QuizViewModel.quizInFocus` (a `StateFlow<Quiz?>`):

- `QuizzesList` row click → `viewModel.setQuizInFocus(quiz)` then `navigate("QuizDetail")`.
- "Add quiz" FAB → `viewModel.setQuizInFocus(null)` then `navigate("EditQuiz")`.
- `EditQuiz` is used for **both create and edit** — `quizInFocus == null` means create, non-null means edit. `QuizViewModel.saveQuiz` branches on this and builds a `quizUpdateMap` of only the changed fields for edit.
- `QuizzesList` calls `viewModel.update()` inside a `LaunchedEffect(Unit)` to reload after returning from Edit. The comment `"So many request. Remember to never do that. Uses LaunchedEffect"` documents why the direct call was removed.

### Navigation

`composeApp/src/commonMain/kotlin/com/example/askceny/App.kt` defines the `NavHost` inline with **string route literals** (no sealed class, no routes object). Existing routes:

`"SignIn"`, `"SignUp"`, `"QuizzesList"`, `"QuizDetail"`, `"EditQuiz"`, `"EditQuestion"` (stub), `"PlayQuiz"` (stub), `"Result"` (stub), `"Search"` (stub).

The `CustomTopBar` is rendered **outside** the `NavHost`, driven by a `when(currentRoute)` block in `MainScreen`. When adding a new route you must update both:

1. A `composable("NewRoute") { ... }` in `MyNavHost`.
2. A matching `"NewRoute" -> { ... }` branch in the `when` that configures the top bar (otherwise no top bar appears).

**Known typo**: the top-bar `when` still matches `"SignIp"` (not `"SignIn"`). It's dead code — the SignIn destination intentionally has no top bar — but don't "fix" it into `"SignIn"` without also removing the println, since matching `"SignIn"` would still add no UI.

### Auth state handling

`AuthState` is a sealed class: `Loading | Authenticated | Unauthenticated | AuthError(errorCode)`.

- `AuthViewModel.init` sets state to `Loading`, then queries `authRepository.getCurrentUser()` and resolves to `Authenticated` or `Unauthenticated`. Screens start before this completes, so `Loading` is a real observable state — don't assume only the two terminal states.
- Auth screens navigate forward by observing `authState` and calling `signInOnClick()` / `signUpOnClick()` when `is AuthState.Authenticated`. This means navigation is a **side effect of state**, not of the button press — the button only calls `viewModel.signIn(...)`.
- `MainScreen` computes `startDestination` from `authState` on every recomposition; `NavHost` captures it on first composition. On cold launch while `authState` is still `Loading`, `startDestination` falls through to `"SignIn"` (Loading != Authenticated).
- Field-level errors live in separate `StateFlow<String>` (`emailError`, `passwordError`, `displayNameError`). `updateXError()` clears them AND demotes `AuthError` back to `Unauthenticated` via `updateState()` — this is how the "error" UI is dismissed when the user edits a field.

### Firestore data shape (`AuthRepositoryImpl` / `QuizRepositoryImpl`)

- Users: collection `users`, document id = Firebase Auth uid, contents = `User(id, displayName, username, email, about, website)`. `username` is generated as `"$displayName${Random.nextInt(1000,9999)}"` on sign-up.
- Quizzes: subcollection `users/{uid}/quizzes`, document id = Firestore auto-id, contents = `Quiz(...)` with `@DocumentId val id`. Firestore auto-populates `id` when reading via `toObject(Quiz::class.java).copy(id = document.id)` (the `.copy` is redundant with `@DocumentId` but harmless).
- Deserialization requires no-arg constructors — every `data class` in `data/models` has default values for every field for that reason. Do not remove the defaults.

## Conventions to match

- **No comments in code unless asked.** The existing codebase has lots of `println("TAG: ...")` debug lines (prefixes like `AUTH_`, `QUIZ_REPOSITORY_`, `QUIZ_DETAIL:`, `MAIN_SCREEN_VERIFY_AUTHENTICATION:`). They are intentional while the app is in development; leave them alone unless asked, and follow the same uppercase-underscore tag convention if you add new ones. `AuthRepositoryImpl` uses `android.util.Log.d/e` — the rest of the code uses `println`. Don't unify them in drive-by changes.
- File layout: one top-level Composable per file, `@Preview` function below it in the same file using `...Fake()` repositories where still applicable so previews render without Firebase.
- **Packages**: 
  - Shared business/data code goes in `shared/src/commonMain/kotlin/com/example/askceny/{data,domain}`.
  - Android-specific data code goes in `shared/src/androidMain/kotlin/com/example/askceny/{data}`.
  - iOS-specific data code goes in `shared/src/iosMain/kotlin/com/example/askceny/{data}`.
  - UI, ViewModels, navigation, themes, and composables go in `composeApp/src/{commonMain,androidMain,iosMain}/kotlin/com/example/askceny/presentation`.
  - Layout: `data/{local, remote, repositories, di}`, `domain/`, `presentation/{theme, viewmodels, composables}` in `composeApp`.
- `local/entities/` holds Room `@Entity` data classes.
- `local/dao/` holds Room `@Dao` interfaces.
- `remote/api/` holds API data sources (e.g., `AuthRemoteDataSource`, `QuizRemoteDataSource`).
- `types/` (if used) holds enums and sealed classes (`AuthState`, `ErrorCode`, `QuestionType`).
- Quiz / User / Question / Answer are plain `data class` (not Parcelable). `Quiz` and `Question` have `var` fields for mutability.

## Gotchas

- `repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)` in `settings.gradle.kts` means project-level `repositories {}` blocks will fail the build — add any new repos to the `dependencyResolutionManagement` block in `settings.gradle.kts`.
- The `.kotlin/sessions/` directory is an IDE artifact (Kotlin daemon state); leave it alone.
- Room database schema is output to `shared/schemas/` — this directory may need to be committed for version control of schema migrations (not yet implemented but planned).
- `expect`/`actual` declarations in `DatabaseBuilder.kt` and `HttpClientProvider.kt` require matching implementations in `androidMain` and `iosMain` — if you add a new expect fun, you must add both actual implementations or the build will fail.

## When adding a new screen

1. Create `composeApp/src/commonMain/kotlin/com/example/askceny/presentation/composables/<Name>.kt` with the screen Composable and a `@Preview` using fake repositories.
2. Add the route in `composeApp/src/commonMain/kotlin/com/example/askceny/App.kt` in `MyNavHost` (`composable("<Name>") { ... }`).
3. Add a `when` branch in `MainScreen` for the top bar if the screen needs one.
4. If it needs new state, extend the existing ViewModel (`AuthViewModel` or `QuizViewModel`) rather than creating a new one, unless the domain is genuinely separate.
5. Pass navigation callbacks in as lambdas (`onClickX: () -> Unit`) — screens should not hold a `NavController`.

## When adding a new repository method

1. Add the method to the interface (`AuthRepository` / `QuizRepository`) in `shared/src/commonMain/data/repositories/`.
2. Implement in `...Impl` (Ktor + Room). If the fake backend is still in use, add a `...Fake` variant as well.
3. For `...Impl`: use Ktor for remote calls and Room DAOs for local caching (implementation pending as part of Ktor/Supabase migration).
4. Map new HTTP/Ktor error codes appropriately in `AuthRepositoryImpl` and/or add entries to `ErrorCode`.
