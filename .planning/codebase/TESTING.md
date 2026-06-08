# Testing Practices

**Analysis Date:** 2026-06-08

## Testing Frameworks

- **Unit Testing:** JUnit 4/5 for pure Kotlin logic (Domain layer, Use Cases).
- **Instrumentation Testing:** AndroidX Test, Espresso.
- **Compose UI Testing:** `androidx.compose.ui:ui-test-junit4`.

## Strategy

- **Domain Layer:** High coverage expected since it's pure Kotlin and contains core business logic.
- **Data Layer:** Mock local DBs (Room in-memory) or mock web servers for remote data.
- **UI Layer:** Compose testing for critical user flows.

## Execution

- Unit Tests: `./gradlew test`
- UI Tests: `./gradlew connectedAndroidTest`

---

*Testing analysis: 2026-06-08*
