package com.thmanyah.shasha.presentation.home

import app.cash.turbine.test
import com.thmanyah.shasha.domain.model.ContentType
import java.io.IOException
import com.thmanyah.shasha.domain.model.HomeSectionsResult
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.domain.usecase.GetHomeSectionsUseCase
import com.thmanyah.shasha.presentation.state.HomeUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import com.thmanyah.shasha.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getHomeSectionsUseCase: GetHomeSectionsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getHomeSectionsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestSection(name: String = "Test", order: Int = 1): Section {
        return Section(
            name = name,
            layoutType = LayoutType.SQUARE,
            contentType = ContentType.PODCAST,
            order = order,
            items = listOf(
                PodcastItem(
                    id = "1",
                    name = "Test Podcast",
                    avatarUrl = "https://example.com/image.jpg",
                    description = "Test",
                    duration = 1000L,
                    episodeCount = 10,
                    language = "en",
                    score = 100.0,
                )
            ),
        )
    }

    @Test
    fun `initial state is Loading then Success on successful fetch`() = runTest {
        val sections = listOf(createTestSection())
        coEvery { getHomeSectionsUseCase(1) } returns Result.success(
            HomeSectionsResult(sections, null, null)
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            val success = awaitItem() as HomeUiState.Success
            assertEquals(1, success.sections.size)
            assertEquals("Test", success.sections[0].name)
        }
    }

    @Test
    fun `emits Error state on failure`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.failure(
            RuntimeException("Network error")
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            val error = awaitItem() as HomeUiState.Error
            assertEquals(R.string.error_unknown, error.messageRes)
        }
    }

    @Test
    fun `emits network Error on IOException`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.failure(
            IOException("No internet")
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            val error = awaitItem() as HomeUiState.Error
            assertEquals(R.string.error_network, error.messageRes)
        }
    }

    @Test
    fun `emits Empty state when no sections returned`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.success(
            HomeSectionsResult(emptyList(), null, null)
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            assertEquals(HomeUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `retry reloads data`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.failure(
            RuntimeException("Error")
        ) andThen Result.success(
            HomeSectionsResult(listOf(createTestSection()), null, null)
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            assertTrue(awaitItem() is HomeUiState.Error)

            viewModel.retry()

            assertEquals(HomeUiState.Loading, awaitItem())
            assertTrue(awaitItem() is HomeUiState.Success)
        }
    }

    @Test
    fun `loadMore appends sections`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.success(
            HomeSectionsResult(
                listOf(createTestSection("Page 1", 1)),
                "/home_sections?page=2",
                10,
            )
        )
        coEvery { getHomeSectionsUseCase(2) } returns Result.success(
            HomeSectionsResult(
                listOf(createTestSection("Page 2", 2)),
                "/home_sections?page=3",
                10,
            )
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            val firstPage = awaitItem() as HomeUiState.Success
            assertEquals(1, firstPage.sections.size)

            viewModel.loadMore()

            val loading = awaitItem() as HomeUiState.Success
            assertTrue(loading.isLoadingMore)

            val secondPage = awaitItem() as HomeUiState.Success
            assertEquals(2, secondPage.sections.size)
            assertEquals("Page 1", secondPage.sections[0].name)
            assertEquals("Page 2", secondPage.sections[1].name)
        }
    }

    @Test
    fun `loadMore uses page number from next_page query`() = runTest {
        coEvery { getHomeSectionsUseCase(1) } returns Result.success(
            HomeSectionsResult(
                sections = listOf(createTestSection("Page 1", 1)),
                nextPage = "/home_sections?page=4",
                totalPages = 10,
            )
        )
        coEvery { getHomeSectionsUseCase(4) } returns Result.success(
            HomeSectionsResult(
                sections = listOf(createTestSection("Page 4", 4)),
                nextPage = "/home_sections?page=5",
                totalPages = 10,
            )
        )

        val viewModel = HomeViewModel(getHomeSectionsUseCase)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            val firstPage = awaitItem() as HomeUiState.Success
            assertEquals(1, firstPage.sections.size)

            viewModel.loadMore()

            val loading = awaitItem() as HomeUiState.Success
            assertTrue(loading.isLoadingMore)

            val loaded = awaitItem() as HomeUiState.Success
            assertEquals(2, loaded.sections.size)
            assertEquals("Page 1", loaded.sections[0].name)
            assertEquals("Page 4", loaded.sections[1].name)
        }

        coVerify(exactly = 1) { getHomeSectionsUseCase(4) }
        coVerify(exactly = 0) { getHomeSectionsUseCase(2) }
    }
}
