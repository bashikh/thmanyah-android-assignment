package com.thmanyah.shasha.data.mapper

import com.thmanyah.shasha.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.shasha.data.remote.dto.PaginationDto
import com.thmanyah.shasha.data.remote.dto.SectionDto
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.GenericItem
import com.thmanyah.shasha.domain.model.PodcastItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SectionMapperTest {

    private val contentMapper = ContentMapper()
    private val sectionMapper = SectionMapper(contentMapper)

    @Test
    fun `maps valid home response correctly`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                SectionDto(
                    name = "Top Podcasts",
                    type = "square",
                    contentType = "podcast",
                    order = 1,
                    content = listOf(
                        mapOf(
                            "podcast_id" to "123",
                            "name" to "Test Podcast",
                            "avatar_url" to "https://example.com/img.jpg",
                            "description" to "A great podcast",
                            "duration" to 1000.0,
                            "episode_count" to 50.0,
                            "language" to "en",
                            "score" to 217.26,
                        )
                    ),
                )
            ),
            pagination = PaginationDto(
                nextPage = "/home_sections?page=2",
                totalPages = 10,
            ),
        )

        val result = sectionMapper.mapHomeSectionsResponse(dto)

        assertEquals(1, result.sections.size)
        assertEquals("Top Podcasts", result.sections[0].name)
        assertEquals(LayoutType.SQUARE, result.sections[0].layoutType)
        assertEquals(ContentType.PODCAST, result.sections[0].contentType)
        assertEquals(1, result.sections[0].order)
        assertEquals("/home_sections?page=2", result.nextPage)
        assertEquals(10, result.totalPages)

        val item = result.sections[0].items[0] as PodcastItem
        assertEquals("123", item.id)
        assertEquals("Test Podcast", item.name)
    }

    @Test
    fun `sorts sections by order`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                SectionDto(
                    name = "Second", type = "square", contentType = "podcast", order = 2,
                    content = listOf(mapOf("podcast_id" to "1", "name" to "P")),
                ),
                SectionDto(
                    name = "First", type = "square", contentType = "podcast", order = 1,
                    content = listOf(mapOf("podcast_id" to "2", "name" to "Q")),
                ),
            ),
            pagination = null,
        )

        val result = sectionMapper.mapHomeSectionsResponse(dto)

        assertEquals("First", result.sections[0].name)
        assertEquals("Second", result.sections[1].name)
    }

    @Test
    fun `handles missing pagination gracefully`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                SectionDto(
                    name = "Test", type = "square", contentType = "podcast", order = 1,
                    content = listOf(mapOf("podcast_id" to "1", "name" to "P")),
                )
            ),
            pagination = null,
        )

        val result = sectionMapper.mapHomeSectionsResponse(dto)

        assertNull(result.nextPage)
        assertNull(result.totalPages)
        assertEquals(1, result.sections.size)
    }

    @Test
    fun `skips sections with no valid items`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                SectionDto(
                    name = "Empty", type = "square", contentType = "podcast",
                    order = 1, content = emptyList(),
                ),
                SectionDto(
                    name = "Valid", type = "square", contentType = "podcast",
                    order = 2, content = listOf(mapOf("podcast_id" to "1", "name" to "P")),
                ),
            ),
            pagination = null,
        )

        val result = sectionMapper.mapHomeSectionsResponse(dto)

        assertEquals(1, result.sections.size)
        assertEquals("Valid", result.sections[0].name)
    }

    @Test
    fun `handles search API inconsistencies - order as string`() {
        val sections = sectionMapper.mapSections(
            listOf(
                SectionDto(
                    name = "d", type = "officia dolore", contentType = "cupidatat",
                    order = "aliqua eiusmod",
                    content = listOf(
                        mapOf(
                            "podcast_id" to "abc",
                            "name" to "Generic Cotton Chips",
                            "avatar_url" to "https://example.com/img.jpg",
                            "episode_count" to "51",
                            "duration" to "77633",
                            "score" to "laboris aute",
                        )
                    ),
                )
            )
        )

        assertEquals(1, sections.size)
        assertEquals(LayoutType.UNKNOWN, sections[0].layoutType)
        assertEquals(ContentType.UNKNOWN, sections[0].contentType)

        val item = sections[0].items[0] as GenericItem
        assertEquals("abc", item.id)
        assertEquals("Generic Cotton Chips", item.name)
        assertEquals(77633L, item.duration)
    }

    @Test
    fun `normalizes big square with space`() {
        val sections = sectionMapper.mapSections(
            listOf(
                SectionDto(
                    name = "Test", type = "big square", contentType = "episode",
                    order = 6,
                    content = listOf(
                        mapOf(
                            "episode_id" to "ep1",
                            "name" to "Episode 1",
                            "podcast_name" to "Podcast",
                            "duration" to 300.0,
                        )
                    ),
                )
            )
        )

        assertEquals(LayoutType.BIG_SQUARE, sections[0].layoutType)
    }

    @Test
    fun `handles null sections list`() {
        val result = sectionMapper.mapHomeSectionsResponse(
            HomeSectionsResponseDto(sections = null, pagination = null)
        )

        assertTrue(result.sections.isEmpty())
    }
}
