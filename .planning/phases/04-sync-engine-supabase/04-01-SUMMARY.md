# Plan 04-01 Summary: Supabase Integration & Authentication Flows

## What Was Built
Integrated the Supabase Kotlin SDK, upgraded Ktor, set up Hilt dependency injection for Supabase, and implemented complete authentication flows. This includes domain-level `AuthRepository` and use cases (`LoginUseCase`, `RegisterUseCase`, `GetSessionUseCase`, `LogoutUseCase`), data-level `SupabaseAuthDataSource` wrapping `SupabaseAuthApi` for testability, and the presentation layer `AuthViewModel` with `LoginScreen` and `RegisterScreen` composables gated in `MainActivity`.

## Tasks Completed
- **Task 1: Add Supabase SDK and WorkManager Dependencies** — Added Supabase Kotlin SDK dependencies (auth, postgrest, storage) via BOM 3.1.1. Upgraded Ktor 2.3.12 -> 3.0.0 and swapped OkHttp client engine for Android client engine. Configured WorkManager and Hilt Work integration. Configured `SUPABASE_URL` and `SUPABASE_ANON_KEY` as BuildConfig fields.
- **Task 2: Create Supabase Hilt Module and Auth Data Source (TDD)** — Created `SupabaseModule` Hilt module. Created `SupabaseAuthApi` and `SupabaseAuthApiImpl` to wrap the Supabase client for mock testing. Implemented `SupabaseAuthDataSource` to catch exceptions and throw domain-specific `AuthException` (D-24). 6 unit tests passing.
- **Task 3: Domain Layer — AuthRepository, UserProfile Model, and Auth UseCases (TDD)** — Created `UserProfile` domain model. Implemented `AuthRepositoryImpl` delegating to `SupabaseAuthDataSource`. Built 4 use cases (`LoginUseCase`, `RegisterUseCase`, `GetSessionUseCase`, `LogoutUseCase`). Enforced username availability check (D-17) and deferred email verification (D-03). Wired `@Binds` in `RepositoryModule`. 17 unit tests passing.
- **Task 4: Auth Presentation Layer — Login, Register Screens, and AuthViewModel (TDD)** — Created `AuthViewModel` with `AuthUiState`. Implemented `LoginScreen` and `RegisterScreen` with inline validation and Material 3 styling. Gated MainActivity access behind a session check. 7 unit tests passing.

## Files Created/Modified
- `app/build.gradle.kts` — Added Supabase BOM 3.1.1, Ktor 3.0.0, WorkManager 2.10.0, and Hilt Work. Forced browser version to 1.8.0.
- `build.gradle.kts` — Added Kotlin serialization plugin to classpath.
- `app/src/main/java/com/patagonia/app/di/SupabaseModule.kt` — Hilt module providing SupabaseClient.
- `app/src/main/java/com/patagonia/app/data/remote/SupabaseAuthApi.kt` / `SupabaseAuthApiImpl.kt` — Thin API wrapper.
- `app/src/main/java/com/patagonia/app/data/remote/SupabaseAuthDataSource.kt` — Exception-mapping data source facade.
- `app/src/main/java/com/patagonia/app/domain/model/UserProfile.kt` — Extended UserProfile model.
- `app/src/main/java/com/patagonia/app/domain/repository/AuthRepository.kt` — Domain repository interface.
- `app/src/main/java/com/patagonia/app/data/repository/AuthRepositoryImpl.kt` — Concrete repository implementation.
- `app/src/main/java/com/patagonia/app/domain/usecase/auth/` — Login/Register/GetSession/Logout UseCases.
- `app/src/main/java/com/patagonia/app/data/di/RepositoryModule.kt` — Added AuthRepository binds.
- `app/src/main/java/com/patagonia/app/presentation/auth/` — AuthUiState, AuthViewModel, LoginScreen, RegisterScreen.
- `app/src/main/java/com/patagonia/app/presentation/MainActivity.kt` — Wired AuthGate check using AuthViewModel.
- `app/src/test/java/com/patagonia/app/...` — 30 unit tests covering data source, repository, use cases, and ViewModel.

## Deviations
- **Supabase BOM pinned to 3.1.1 (instead of 3.6.0)**: Bumping to 3.6.0 forces Ktor 3.4.3 which requires Kotlin 2.3 metadata, violating compatibility with Kotlin 2.1.0 and compileSdk 35. Force-pinned to 3.1.1.
- **androidx.browser force-pinned to 1.8.0**: Transitive 1.10.0 from Supabase requires compileSdk 36. Forced to 1.8.0.
- **Mockito suspend test adaptation**: Changed `thenThrow` mocks to `thenAnswer { throw ... }` for coroutines JVM testing.

## Verification Results
- `./gradlew :app:testDebugUnitTest` — **BUILD SUCCESSFUL** — 134 tests, 0 failures (30 dedicated auth tests).
- `./gradlew :app:assembleDebug` — **BUILD SUCCESSFUL** in 1m 19s.

## Self-Check: PASSED
