# Thmanyah Android Assignment

A podcast content browsing app built with Kotlin and Jetpack Compose, implementing backend-driven UI rendering, clean architecture, and resilient handling of unreliable API responses.

---

## Tech Stack

| Layer | Libraries |
|-------|-----------|
| UI | Jetpack Compose, Material 3, Coil, Navigation Compose |
| Architecture | ViewModel, StateFlow, Kotlin Coroutines + Flow |
| DI | Hilt |
| Network | Retrofit, OkHttp, Moshi (codegen) |
| Testing | JUnit, MockK, Turbine, Coroutines Test |

---

## Architecture

Clean Architecture with MVVM and unidirectional data flow.

```
Presentation → Domain → Data
```

```
app/
├── core/
│   ├── network/          # safeApiCall wrapper
│   └── util/             # SafeParsing, ErrorMapper, LocaleManager
├── data/
│   ├── remote/           # Retrofit services + DTOs
│   ├── mapper/           # DTO → Domain mappers
│   └── repository/       # Repository implementations
├── domain/
│   ├── model/            # Section, SectionItem, LayoutType, ContentType
│   ├── repository/       # Repository interfaces
│   └── usecase/          # GetHomeSectionsUseCase, SearchSectionsUseCase
├── presentation/
│   ├── home/             # HomeScreen, HomeViewModel
│   ├── search/           # SearchScreen, SearchViewModel
│   ├── components/       # SectionRenderer, cards, TopBar, MiniPlayer
│   └── navigation/       # AppNavigation
└── di/                   # Hilt modules
```

Data flow:

```
Retrofit → DTO → Mapper → Domain Model → UseCase → ViewModel (StateFlow) → Compose UI
```

---

## Key Features

**Backend-Driven UI** — Sections render dynamically from API metadata (`type`, `content_type`, `order`). A central `SectionRenderer` dispatches to layout-specific composables. No layouts or titles are hardcoded.

Supported layouts: `SQUARE`, `BIG_SQUARE`, `TWO_LINES_GRID`, `QUEUE`, `FALLBACK`

**Search** — `debounce(200ms)` + `distinctUntilChanged` + `flatMapLatest` to cancel stale requests and minimize API calls.

**Pagination** — Custom infinite scroll on the Home screen. `next_page` URL is parsed to extract the page number, with a fallback to increment from the current page if parsing fails.

**Resilient Parsing** — The mapper layer acts as the safety boundary. DTO fields are nullable, safe conversion utilities handle malformed types, and invalid items are skipped with debug logging rather than crashing.

---

## Running the Project

1. Open the project in **Android Studio Hedgehog** or later
2. Sync Gradle
3. Run the `app` module on an emulator or device (API 24+)

```bash
# Unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests
./gradlew :app:connectedDebugAndroidTest
```

---

## Notes

The Search API is a mock endpoint that returns inconsistent data — numeric fields as strings, random layout types, malformed names. The mapper normalizes all responses before they reach the domain layer. The Home API also inconsistently returns `"big square"` (with space) and `"big_square"` (with underscore) for the same layout; `LayoutType.fromApi()` normalizes both.
