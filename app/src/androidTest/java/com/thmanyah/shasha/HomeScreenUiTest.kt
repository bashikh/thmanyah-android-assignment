package com.thmanyah.shasha

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.presentation.components.SectionRenderer
import com.thmanyah.shasha.ui.theme.ThmanyahTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sectionRenderer_displaysHeaderAndItems() {
        val section = createTestSection(
            name = "Top Podcasts",
            layoutType = LayoutType.SQUARE,
            items = listOf(
                createTestPodcast(id = "1", name = "Podcast One"),
                createTestPodcast(id = "2", name = "Podcast Two"),
            ),
        )

        composeTestRule.setContent {
            ThmanyahTheme {
                SectionRenderer(section = section)
            }
        }

        composeTestRule.onNodeWithText("Top Podcasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Podcast One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Podcast Two").assertIsDisplayed()
    }

    @Test
    fun sectionRenderer_displaysSeeAllButton() {
        val section = createTestSection(
            name = "Trending",
            layoutType = LayoutType.SQUARE,
            items = listOf(createTestPodcast(id = "1", name = "Item 1")),
        )

        composeTestRule.setContent {
            ThmanyahTheme {
                SectionRenderer(section = section)
            }
        }

        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
    }

    @Test
    fun sectionRenderer_bigSquareLayout_displaysItems() {
        val section = createTestSection(
            name = "Audiobooks",
            layoutType = LayoutType.BIG_SQUARE,
            items = listOf(
                createTestPodcast(id = "1", name = "Book Alpha"),
            ),
        )

        composeTestRule.setContent {
            ThmanyahTheme {
                SectionRenderer(section = section)
            }
        }

        composeTestRule.onNodeWithText("Audiobooks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Book Alpha").assertIsDisplayed()
    }

    @Test
    fun sectionRenderer_unknownLayout_fallsBackSafely() {
        val section = createTestSection(
            name = "Unknown Section",
            layoutType = LayoutType.UNKNOWN,
            items = listOf(
                createTestPodcast(id = "1", name = "Fallback Item"),
            ),
        )

        composeTestRule.setContent {
            ThmanyahTheme {
                SectionRenderer(section = section)
            }
        }

        composeTestRule.onNodeWithText("Unknown Section").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fallback Item").assertIsDisplayed()
    }

    // -- Test helpers --

    private fun createTestSection(
        name: String,
        layoutType: LayoutType,
        items: List<PodcastItem>,
    ) = Section(
        name = name,
        layoutType = layoutType,
        contentType = ContentType.PODCAST,
        order = 1,
        items = items,
    )

    private fun createTestPodcast(
        id: String,
        name: String,
    ) = PodcastItem(
        id = id,
        name = name,
        avatarUrl = "",
        description = "",
        duration = 0,
        episodeCount = 10,
        language = "en",
        score = 0.0,
    )
}
