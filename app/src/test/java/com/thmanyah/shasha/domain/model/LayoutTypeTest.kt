package com.thmanyah.shasha.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LayoutTypeTest {

    @Test
    fun `fromApi maps square correctly`() {
        assertEquals(LayoutType.SQUARE, LayoutType.fromApi("square"))
    }

    @Test
    fun `fromApi maps big_square correctly`() {
        assertEquals(LayoutType.BIG_SQUARE, LayoutType.fromApi("big_square"))
    }

    @Test
    fun `fromApi maps big square with space correctly`() {
        assertEquals(LayoutType.BIG_SQUARE, LayoutType.fromApi("big square"))
    }

    @Test
    fun `fromApi maps 2_lines_grid correctly`() {
        assertEquals(LayoutType.TWO_LINES_GRID, LayoutType.fromApi("2_lines_grid"))
    }

    @Test
    fun `fromApi maps queue correctly`() {
        assertEquals(LayoutType.QUEUE, LayoutType.fromApi("queue"))
    }

    @Test
    fun `fromApi maps unknown type to UNKNOWN`() {
        assertEquals(LayoutType.UNKNOWN, LayoutType.fromApi("voluptate"))
    }

    @Test
    fun `fromApi handles null`() {
        assertEquals(LayoutType.UNKNOWN, LayoutType.fromApi(null))
    }

    @Test
    fun `fromApi is case insensitive`() {
        assertEquals(LayoutType.SQUARE, LayoutType.fromApi("SQUARE"))
        assertEquals(LayoutType.BIG_SQUARE, LayoutType.fromApi("Big Square"))
        assertEquals(LayoutType.QUEUE, LayoutType.fromApi("QUEUE"))
    }
}
