# Architecture — Thmanyah Android Assignment

---

## Table of Contents

1. [API Analysis](#1-api-analysis)
2. [Layer Structure](#2-layer-structure)
3. [Package Structure](#3-package-structure)
4. [Data Flow](#4-data-flow)
5. [Section Rendering](#5-section-rendering)
6. [Pagination](#6-pagination)
7. [Error Handling](#7-error-handling)
8. [UI State Models](#8-ui-state-models)
9. [Domain Models](#9-domain-models)

---

## 1. API Analysis

### Home API

Top-level shape:

```json
{
  "sections": [ ... ],
  "pagination": {
    "next_page": "/home_sections?page=2",
    "total_pages": 10
  }
}
```

#### Section Fields

| Field          | Type   | Description                    |
|----------------|--------|--------------------------------|
| `name`         | String | Section title (backend-driven) |
| `type`         | String | Layout type for rendering      |
| `content_type` | String | Determines item schema         |
| `order`        | Int    | Display order                  |
| `content`      | Array  | List of content items          |

#### Layout Types Observed

| `type` value   | `content_type`           |
|----------------|--------------------------|
| `square`       | podcast, audio_article   |
| `2_lines_grid` | episode, audio_book      |
| `big_square`   | audio_book               |
| `queue`        | podcast                  |
| `"big square"` | episode                  |

Key observation: `"big square"` (with space) and `"big_square"` (with underscore) refer to the same layout. `LayoutType.fromApi()` normalizes both.

#### Item Fields by `content_type`

**podcast:** `podcast_id`, `name`, `description`, `avatar_url`, `episode_count`, `duration`, `language`, `priority`, `popularityScore`, `score`

**episode:** `episode_id`, `name`, `season_number`, `episode_type`, `podcast_name`, `author_name`, `description`, `number`, `duration`, `avatar_url`, `audio_url`, `release_date`, `podcast_id`, `paid_is_early_access`, `paid_is_exclusive`, `score`

**audio_book:** `audiobook_id`, `name`, `author_name`, `description`, `avatar_url`, `duration`, `language`, `release_date`, `score`

**audio_article:** `article_id`, `name`, `author_name`, `description`, `avatar_url`, `duration`, `release_date`, `score`

#### Pagination

- `next_page`: relative URL string — e.g. `"/home_sections?page=2"` (not a plain integer)
- `total_pages`: integer
- Pagination object may be absent — the first page must render correctly regardless

### Search API

Top-level shape:

```json
{
  "sections": [ ... ]
}
```

No pagination field. The Search API is a **mock endpoint** — every field that should be numeric may arrive as arbitrary text.

#### Inconsistencies Observed

| Field             | Home API        | Search API (mock)               |
|-------------------|-----------------|---------------------------------|
| `order`           | Int             | String (e.g. `"aliqua eiusmod"`) |
| `type`            | Known layout    | Random string (e.g. `"voluptate"`) |
| `content_type`    | Known type      | Random string                   |
| `episode_count`   | Int             | String (e.g. `"51"`)            |
| `duration`        | Int             | String (e.g. `"77633"`)         |
| `priority`        | Int             | String (e.g. `"ad quis"`)       |
| `popularityScore` | Int             | String (e.g. `"ea"`)            |
| `score`           | Double          | String (e.g. `"laboris aute"`)  |
| Section `name`    | Descriptive     | Single character (e.g. `"d"`)   |

---

## 2. Layer Structure

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  Compose UI + ViewModels            │
├─────────────────────────────────────┤
│           Domain Layer              │
│  Models + UseCases + Repo Interfaces│
├─────────────────────────────────────┤
│            Data Layer               │
│  Retrofit + DTOs + Mappers          │
├─────────────────────────────────────┤
│           Core Layer                │
│  Network utilities + Safe Parsing   │
└─────────────────────────────────────┘
```

**Dependency rule:** inner layers never reference outer layers. Repository interfaces live in domain; implementations live in data. No DTO types leak beyond the data layer.

### Dependency Injection

All Hilt modules live under `di/` only:

- **`NetworkModule`** — provides Retrofit, OkHttpClient, Moshi, `HomeApiService`, `SearchApiService` as singletons
- **`RepositoryModule`** — binds `HomeRepository` → `HomeRepositoryImpl`, `SearchRepository` → `SearchRepositoryImpl`

`core/network/` contains utilities only — no Hilt modules.

---

## 3. Package Structure

```
app/
├── core/
│   ├── network/
│   │   ├── SafeApiCall.kt            # Result wrapper for all API calls
│   │   └── LoggingInterceptor.kt     # OkHttp logging (debug only)
│   └── util/
│       ├── SafeParsing.kt            # toSafeInt, toSafeLong, toSafeDouble
│       ├── ErrorMapper.kt            # Exception → localized string resource
│       └── LocaleManager.kt          # AR/EN runtime switching
│
├── data/
│   ├── remote/
│   │   ├── HomeApiService.kt
│   │   ├── SearchApiService.kt
│   │   └── dto/
│   │       ├── HomeSectionsResponseDto.kt
│   │       ├── SectionDto.kt
│   │       ├── PaginationDto.kt
│   │       ├── PodcastDto.kt
│   │       ├── EpisodeDto.kt
│   │       ├── AudioBookDto.kt
│   │       └── AudioArticleDto.kt
│   ├── mapper/
│   │   ├── SectionMapper.kt          # DTO → Domain + normalization
│   │   └── ContentMapper.kt          # Content items DTO → Domain
│   └── repository/
│       ├── HomeRepositoryImpl.kt
│       └── SearchRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── Section.kt
│   │   ├── SectionItem.kt            # Sealed interface
│   │   ├── LayoutType.kt             # Enum with fromApi() + UNKNOWN fallback
│   │   └── ContentType.kt            # Enum with fromApi() + UNKNOWN fallback
│   ├── repository/
│   │   ├── HomeRepository.kt
│   │   └── SearchRepository.kt
│   └── usecase/
│       ├── GetHomeSectionsUseCase.kt
│       └── SearchSectionsUseCase.kt
│
├── presentation/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── search/
│   │   ├── SearchScreen.kt
│   │   └── SearchViewModel.kt
│   ├── components/
│   │   ├── SectionRenderer.kt        # Central dispatch composable
│   │   ├── SectionHeader.kt
│   │   ├── SquareSection.kt          # LazyRow of square cards
│   │   ├── BigSquareSection.kt       # LazyRow of large cards
│   │   ├── TwoLinesGridSection.kt    # LazyRow of 2-row stacked cards
│   │   ├── QueueSection.kt           # Vertical column of media rows
│   │   ├── FallbackSection.kt        # Safe default for unknown layouts
│   │   ├── TopBar.kt
│   │   └── MiniPlayer.kt
│   ├── navigation/
│   │   └── AppNavigation.kt
│   └── state/
│       ├── HomeUiState.kt
│       └── SearchUiState.kt
│
├── di/
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
└── MainActivity.kt
```

---

## 4. Data Flow

```
API Response (JSON)
       │
       ▼
  Retrofit + Moshi  →  deserializes to DTO
       │
       ▼
  Mapper            →  DTO → Domain model (safe parsing + normalization)
       │
       ▼
  Repository Impl   →  wraps result in Result<T>
       │
       ▼
  UseCase           →  single-purpose operator fun invoke()
       │
       ▼
  ViewModel         →  exposes StateFlow<UiState>
       │
       ▼
  Compose UI        →  observes state, renders via SectionRenderer
```

UseCases are single-purpose callable classes using `operator fun invoke`, keeping ViewModels thin:

```kotlin
class GetHomeSectionsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<HomeSectionsResult>
}

class SearchSectionsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<Section>>
}
```

---

## 5. Section Rendering

`SectionRenderer` is the central dispatch composable. It receives a `Section` domain model and routes to the appropriate layout based on `LayoutType`:

| LayoutType       | Component            | Container                          |
|------------------|----------------------|------------------------------------|
| `SQUARE`         | `SquareSection`      | `LazyRow` of 120dp square cards    |
| `BIG_SQUARE`     | `BigSquareSection`   | `LazyRow` of 180dp featured cards  |
| `TWO_LINES_GRID` | `TwoLinesGridSection`| `LazyRow` of `Column(2 cards)` pairs |
| `QUEUE`          | `QueueSection`       | Vertical `Column` of media rows    |
| `UNKNOWN`        | `FallbackSection`    | Safe horizontal fallback           |

`TWO_LINES_GRID` chunks items into pairs before rendering — each `LazyRow` item is a `Column` of 2 stacked cards.

`QUEUE` renders as a vertical list of media rows, not a horizontal scroll.

### Layout Type Normalization

```kotlin
enum class LayoutType {
    SQUARE, BIG_SQUARE, TWO_LINES_GRID, QUEUE, UNKNOWN;

    companion object {
        fun fromApi(value: String?): LayoutType =
            when (value?.lowercase()?.replace(" ", "_")) {
                "square"        -> SQUARE
                "big_square"    -> BIG_SQUARE
                "2_lines_grid"  -> TWO_LINES_GRID
                "queue"         -> QUEUE
                else            -> UNKNOWN
            }
    }
}
```

### Serialization: Moshi Only

All JSON parsing uses Moshi exclusively — no Gson or Kotlinx Serialization.

- DTOs annotated with `@JsonClass(generateAdapter = true)` for compile-time adapters
- `KotlinJsonAdapterFactory` as reflection fallback
- Moshi instance created once in `NetworkModule`, shared across all Retrofit instances
- Unknown JSON fields are silently ignored

---

## 6. Pagination

Custom pagination — not Paging 3.

`HomeViewModel` maintains:
- `currentPage: Int`
- `totalPages: Int?`
- `isLoadingMore: Boolean`
- `sections: List<Section>` (accumulated across pages)

`LazyListState` detects when the last visible item is near the end of the list and triggers the next page load. New sections are appended to the existing list.

`next_page` is a relative URL string — a parser extracts the page number from the query string. When parsing fails, the system increments from the current page bounded by `total_pages`.

**Fallback rules:**

| Condition | Behavior |
|-----------|----------|
| `pagination` absent | Render first page as complete. No further loading. |
| `next_page` null | Stop pagination. |
| `total_pages` unparseable | Rely on `next_page` presence only. |
| Subsequent page fails | Keep existing sections visible. Show retry footer. |

---

## 7. Error Handling

**Network level** — `safeApiCall()` catches `IOException`, `HttpException`, and general exceptions, returning `Result<T>`.

**Parsing level** — Mapper wraps each item in `try/catch`. Invalid items return `null` and are filtered via `mapNotNull`. Sections with zero valid items are skipped. Failures are logged with `Log.w()` in debug builds.

**Search API normalization** — applied in the mapper before data reaches the domain layer:

| Issue | Action |
|-------|--------|
| Unknown `type` | → `LayoutType.UNKNOWN` → `FallbackSection` |
| Unknown `content_type` | → `ContentType.UNKNOWN` → `GenericItem` |
| `order` as non-numeric | `toIntOrNull()`, fallback to index |
| `episode_count` as string | `toIntOrNull()`, fallback to `0` |
| `duration` as string | `toLongOrNull()`, fallback to `0L` |
| `score` as non-numeric | `toDoubleOrNull()`, fallback to `0.0` |

**ViewModel level** — sealed `UiState` models all states explicitly. Error state always includes a retry action.

**UI level** — one bad section never crashes the screen. Error → retry button. Empty → message. Loading → shimmer skeletons.

---

## 8. UI State Models

```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val sections: List<Section>,
        val isLoadingMore: Boolean = false
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val sections: List<Section>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}
```

---

## 9. Domain Models

```kotlin
@Immutable
data class Section(
    val name: String,
    val layoutType: LayoutType,
    val contentType: ContentType,
    val order: Int,
    val items: List<SectionItem>
)

sealed interface SectionItem {
    val id: String
    val name: String
    val avatarUrl: String
}

@Immutable data class PodcastItem(...)   : SectionItem
@Immutable data class EpisodeItem(...)   : SectionItem
@Immutable data class AudioBookItem(...) : SectionItem
@Immutable data class ArticleItem(...)   : SectionItem
@Immutable data class GenericItem(...)   : SectionItem  // unknown content_type
```

`@Immutable` confirms to the Compose compiler that these data classes are structurally immutable, allowing it to skip recomposition of composables that receive unchanged instances.
