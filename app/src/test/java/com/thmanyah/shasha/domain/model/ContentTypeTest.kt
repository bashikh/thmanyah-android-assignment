package com.thmanyah.shasha.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ContentTypeTest {

    @Test
    fun `fromApi maps all known types`() {
        assertEquals(ContentType.PODCAST, ContentType.fromApi("podcast"))
        assertEquals(ContentType.EPISODE, ContentType.fromApi("episode"))
        assertEquals(ContentType.AUDIO_BOOK, ContentType.fromApi("audio_book"))
        assertEquals(ContentType.AUDIO_ARTICLE, ContentType.fromApi("audio_article"))
    }

    @Test
    fun `fromApi maps unknown types`() {
        assertEquals(ContentType.UNKNOWN, ContentType.fromApi("magna aute elit"))
        assertEquals(ContentType.UNKNOWN, ContentType.fromApi(null))
    }
}
