# Plan 04-02 Execution Summary

## Metadata
* **Phase:** 04 (Sync Engine & Supabase Integration)
* **Plan:** 02 (Database Schema Setup & User Profile Experience)
* **Completed At:** 2026-07-07T13:40:00-04:00

## Tasks Completed

### Task 1: Extend Room Schema with Sync Fields
- Created `SyncStatus` enum for tracking capture sync states: `PENDING_INSERT`, `PENDING_UPDATE`, `SYNCED`, `PENDING_DELETE`, `SYNC_FAILED`.
- Created `SyncStatusConverter` to serialize/deserialize `SyncStatus` as `String` in Room database.
- Evolved `CaptureEntity` by removing the boolean `isSynced` flag and adding `syncStatus: SyncStatus`, `remoteId: String?`, `isShared: Boolean`, `isDeleted: Boolean`, and `userId: String?`.
- Updated `Capture` domain model and `CaptureEntityMapper` to align with the new schema.
- Added new sync-specific DAO queries to `CaptureDao`: `getPendingSyncCaptures`, `markAsSynced`, `markAsSyncFailed`, `getByRemoteId`, `softDelete`.
- Promoted Room database to version 2 and registered a table-recreation migration `MIGRATION_1_2` to safely drop the old `isSynced` column and migrate data without loss.
- Wrote failing unit tests for `SyncStatus` and `CaptureDao` (including a raw SQLite test for `MIGRATION_1_2` data integrity) first, then verified they pass.

### Task 2: Supabase Remote Capture Data Source
- Created `@Serializable` `RemoteCaptureDto` containing: `id` (UUID), `user_id` (FK to auth), `species_name`, `image_path`, `latitude`, `longitude` (exact coordinates), `captured_at`, `confidence`, `notes`, `is_shared`, and `is_deleted`.
- Created `SupabaseCaptureApi` and its SDK implementation `SupabaseCaptureApiImpl` to separate the Supabase Postgrest client from JVM testing boundaries.
- Created `SupabaseCaptureDataSource` delegating to the API wrapper to insert, update, soft-delete, retrieve all captures for a user, and toggle sharing status.
- Documented the exact PostgreSQL table SQL schema and RLS policies as class-level comments in `SupabaseCaptureDataSource.kt`.
- Registered dependency binding in `SupabaseModule` and verified mocked behaviors in `SupabaseCaptureDataSourceTest`.

### Task 3: Update CaptureRepository for Sync-Aware Operations
- Added sync-aware methods to `CaptureRepository`: `saveCapture` (sets `PENDING_INSERT`), `updateCapture` (sets `PENDING_UPDATE`), `deleteCapture` (soft-deletes to `PENDING_DELETE`), `toggleSharing` (updates `isShared` and sets `PENDING_UPDATE`), and `getPendingSyncCaptures`.
- Added backward-compatibility wrappers to `CaptureRepository` and `CaptureRepositoryImpl` to prevent compile breakages on existing screens.
- Created `SaveCaptureUseCase` and updated `DeleteCaptureUseCase` to call repository methods.
- Wrote and verified unit tests in `CaptureRepositoryImplTest`, `SaveCaptureUseCaseTest`, and `DeleteCaptureUseCaseTest`.

### Task 4: Profile and Settings Screens
- Created `ProfileViewModel` and `SettingsViewModel` observing the current user session and local captures.
- Implemented `ProfileScreen` in Compose featuring user avatar (with text initials fallback), username, biography, level badge, total captures count, and an automatic error banner when `SYNC_FAILED` captures exist.
- Implemented `SettingsScreen` in Compose featuring read-only user info, a privacy toggle (calls `UpdateProfileUseCase`), a logout button, and a manual "Retry Sync" panel that resets `SYNC_FAILED` captures back to their respective pending states (`PENDING_INSERT`, `PENDING_UPDATE`, or `PENDING_DELETE`).
- Integrated `PROFILE` and `SETTINGS` into `TestingScreen` navigation and updated `MainActivity.kt` with a new "Perfil" tab.
- Verified ViewModels under unconfined coroutines test dispatchers in `ProfileViewModelTest` and `SettingsViewModelTest`.

## Files Created/Modified

### Created
- [SyncStatus.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/model/SyncStatus.kt)
- [SyncStatusConverter.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/local/converter/SyncStatusConverter.kt)
- [RemoteCaptureDto.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/dto/RemoteCaptureDto.kt)
- [SupabaseCaptureApi.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseCaptureApi.kt)
- [SupabaseCaptureApiImpl.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseCaptureApiImpl.kt)
- [SupabaseCaptureDataSource.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseCaptureDataSource.kt)
- [SaveCaptureUseCase.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/usecase/SaveCaptureUseCase.kt)
- [UpdateProfileUseCase.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/usecase/profile/UpdateProfileUseCase.kt)
- [ProfileScreen.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/profile/ProfileScreen.kt)
- [ProfileViewModel.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/profile/ProfileViewModel.kt)
- [SettingsScreen.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/settings/SettingsScreen.kt)
- [SettingsViewModel.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/settings/SettingsViewModel.kt)
- [SyncStatusTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/data/local/SyncStatusTest.kt)
- [CaptureDaoTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/data/local/CaptureDaoTest.kt)
- [SupabaseCaptureDataSourceTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/data/remote/SupabaseCaptureDataSourceTest.kt)
- [CaptureRepositoryImplTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/data/repository/CaptureRepositoryImplTest.kt)
- [SaveCaptureUseCaseTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/domain/usecase/SaveCaptureUseCaseTest.kt)
- [DeleteCaptureUseCaseTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/domain/usecase/DeleteCaptureUseCaseTest.kt)
- [UpdateProfileUseCaseTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/domain/usecase/profile/UpdateProfileUseCaseTest.kt)
- [ProfileViewModelTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/presentation/profile/ProfileViewModelTest.kt)
- [SettingsViewModelTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/presentation/settings/SettingsViewModelTest.kt)

### Modified
- [CaptureEntity.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/local/entity/CaptureEntity.kt)
- [CaptureEntityMapper.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/local/entity/CaptureEntityMapper.kt)
- [Capture.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/model/Capture.kt)
- [CaptureDao.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/local/dao/CaptureDao.kt)
- [PatagoniaDatabase.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/local/PatagoniaDatabase.kt)
- [DatabaseModule.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/di/DatabaseModule.kt)
- [SupabaseAuthApi.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseAuthApi.kt)
- [SupabaseAuthApiImpl.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseAuthApiImpl.kt)
- [SupabaseAuthDataSource.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/remote/SupabaseAuthDataSource.kt)
- [AuthRepository.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/repository/AuthRepository.kt)
- [AuthRepositoryImpl.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/repository/AuthRepositoryImpl.kt)
- [DeleteCaptureUseCase.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/usecase/DeleteCaptureUseCase.kt)
- [CaptureRepository.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/domain/repository/CaptureRepository.kt)
- [CaptureRepositoryImpl.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/data/repository/CaptureRepositoryImpl.kt)
- [CameraViewModel.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/viewmodel/CameraViewModel.kt)
- [MainActivity.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/presentation/MainActivity.kt)
- [SupabaseModule.kt](file:///home/pingu/Projects/PatagonIA/app/src/main/java/com/patagonia/app/di/SupabaseModule.kt)
- [build.gradle.kts](file:///home/pingu/Projects/PatagonIA/app/build.gradle.kts) (added testing dependencies: Robolectric & Android Test Core/Junit)
- [MapPinTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/domain/model/MapPinTest.kt)
- [MapViewModelTest.kt](file:///home/pingu/Projects/PatagonIA/app/src/test/java/com/patagonia/app/presentation/viewmodel/MapViewModelTest.kt)

## Verification Results

### JVM Unit Tests
Ran all unit tests via:
`JAVA_HOME=/home/pingu/Downloads/android-studio/jbr ./gradlew :app:testDebugUnitTest`

All **36 tests completed successfully**:
```
BUILD SUCCESSFUL in 6s
31 actionable tasks: 9 executed, 22 up-to-date
```

Specifically verified tests:
- `com.patagonia.app.data.local.SyncStatusTest` (PASS)
- `com.patagonia.app.data.local.CaptureDaoTest` (PASS, 8 tests including `testRoomMigration1To2`)
- `com.patagonia.app.data.remote.SupabaseCaptureDataSourceTest` (PASS)
- `com.patagonia.app.data.repository.CaptureRepositoryImplTest` (PASS)
- `com.patagonia.app.domain.usecase.SaveCaptureUseCaseTest` (PASS)
- `com.patagonia.app.domain.usecase.DeleteCaptureUseCaseTest` (PASS)
- `com.patagonia.app.domain.usecase.profile.UpdateProfileUseCaseTest` (PASS)
- `com.patagonia.app.presentation.profile.ProfileViewModelTest` (PASS)
- `com.patagonia.app.presentation.settings.SettingsViewModelTest` (PASS)

### Compilation Validation
Verified full debug compilation compiles without errors:
`JAVA_HOME=/home/pingu/Downloads/android-studio/jbr ./gradlew :app:compileDebugKotlin :app:compileDebugJavaWithJavac`
```
BUILD SUCCESSFUL in 3s
18 actionable tasks: 3 executed, 15 up-to-date
```
