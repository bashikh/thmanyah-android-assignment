package com.thmanyah.shasha.presentation.search

import app.cash.turbine.test
import com.thmanyah.shasha.R
import com.thmanyah.shasha.domain.model.ContentType
import java.io.IOException
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.domain.usecase.SearchSectionsUseCase
import com.thmanyah.shasha.presentation.state.SearchUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchSectionsUseCase: SearchSectionsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchSectionsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestSection(): Section {
        return Section(
            name = "Results",
            layoutType = LayoutType.SQUARE,
            contentType = ContentType.PODCAST,
            order = 1,
            items = listOf(
                PodcastItem(
                    id = "1", name = "Test", avatarUrl = "", description = "",
                    duration = 0, episodeCount = 0, language = "", score = 0.0,
                )
            ),
        )
    }

    @Test
    fun `initial state is Idle`() = runTest {
        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `clearing query returns Idle state`() = runTest {
        val sections = listOf(createTestSection())
        coEvery { searchSectionsUseCase("test") } returns Result.success(sections)

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("test")
            advanceTimeBy(300)
            assertEquals(SearchUiState.Loading, awaitItem())
            awaitItem() // Success

            viewModel.onQueryChanged("")
            advanceTimeBy(300)
            assertEquals(SearchUiState.Idle, awaitItem())
        }
    }

    @Test
    fun `search returns Success state`() = runTest {
        val sections = listOf(createTestSection())
        coEvery { searchSectionsUseCase("test") } returns Result.success(sections)

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("test")
            advanceTimeBy(300)

            assertEquals(SearchUiState.Loading, awaitItem())
            val success = awaitItem() as SearchUiState.Success
            assertEquals(1, success.sections.size)
        }
    }

    @Test
    fun `search returns Empty when no results`() = runTest {
        coEvery { searchSectionsUseCase("xyz") } returns Result.success(emptyList())

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("xyz")
            advanceTimeBy(300)

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `search returns Error state on failure`() = runTest {
        coEvery { searchSectionsUseCase("fail") } returns Result.failure(
            RuntimeException("Search failed")
        )

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("fail")
            advanceTimeBy(300)

            assertEquals(SearchUiState.Loading, awaitItem())
            val error = awaitItem() as SearchUiState.Error
            assertEquals(R.string.error_unknown, error.messageRes)
        }
    }

    @Test
    fun `search returns network Error on IOException`() = runTest {
        coEvery { searchSectionsUseCase("fail") } returns Result.failure(
            IOException("No internet")
        )

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("fail")
            advanceTimeBy(300)

            assertEquals(SearchUiState.Loading, awaitItem())
            val error = awaitItem() as SearchUiState.Error
            assertEquals(R.string.error_network, error.messageRes)
        }
    }

    @Test
    fun `debounce prevents rapid API calls`() = runTest {
        val sections = listOf(createTestSection())
        coEvery { searchSectionsUseCase(any()) } returns Result.success(sections)

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("t")
            advanceTimeBy(50)
            viewModel.onQueryChanged("te")
            advanceTimeBy(50)
            viewModel.onQueryChanged("tes")
            advanceTimeBy(50)
            viewModel.onQueryChanged("test")
            advanceTimeBy(300)

            assertEquals(SearchUiState.Loading, awaitItem())
            awaitItem() // Success

            coVerify(exactly = 1) { searchSectionsUseCase("test") }
            coVerify(exactly = 0) { searchSectionsUseCase("t") }
            coVerify(exactly = 0) { searchSectionsUseCase("te") }
            coVerify(exactly = 0) { searchSectionsUseCase("tes") }
        }
    }

    @Test
    fun `distinctUntilChanged prevents duplicate calls`() = runTest {
        val sections = listOf(createTestSection())
        coEvery { searchSectionsUseCase("test") } returns Result.success(sections)

        val viewModel = SearchViewModel(searchSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChanged("test")
            advanceTimeBy(300)
            assertEquals(SearchUiState.Loading, awaitItem())
            awaitItem() // Success

            viewModel.onQueryChanged("test")
            advanceTimeBy(300)

            coVerify(exactly = 1) { searchSectionsUseCase("test") }
        }
    }
}
