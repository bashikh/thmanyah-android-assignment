package com.thmanyah.shasha.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SafeParsingTest {

    @Test
    fun `toSafeInt handles Int`() {
        assertEquals(42, (42 as Any).toSafeInt())
    }

    @Test
    fun `toSafeInt handles Double`() {
        assertEquals(42, (42.7 as Any).toSafeInt())
    }

    @Test
    fun `toSafeInt handles numeric String`() {
        assertEquals(51, ("51" as Any).toSafeInt())
    }

    @Test
    fun `toSafeInt handles non-numeric String`() {
        assertEquals(0, ("ad quis" as Any).toSafeInt())
    }

    @Test
    fun `toSafeInt handles null`() {
        assertEquals(0, null.toSafeInt())
    }

    @Test
    fun `toSafeInt uses custom default`() {
        assertEquals(-1, ("invalid" as Any).toSafeInt(-1))
    }

    @Test
    fun `toSafeLong handles Long`() {
        assertEquals(77633L, (77633L as Any).toSafeLong())
    }

    @Test
    fun `toSafeLong handles numeric String`() {
        assertEquals(77633L, ("77633" as Any).toSafeLong())
    }

    @Test
    fun `toSafeLong handles non-numeric String`() {
        assertEquals(0L, ("laboris aute" as Any).toSafeLong())
    }

    @Test
    fun `toSafeDouble handles Double`() {
        assertEquals(217.26, (217.26 as Any).toSafeDouble(), 0.01)
    }

    @Test
    fun `toSafeDouble handles numeric String`() {
        assertEquals(217.26, ("217.26" as Any).toSafeDouble(), 0.01)
    }

    @Test
    fun `toSafeDouble handles non-numeric String`() {
        assertEquals(0.0, ("laboris aute ut deserunt" as Any).toSafeDouble(), 0.01)
    }

    @Test
    fun `toSafeString handles various inputs`() {
        assertEquals("hello", ("hello" as Any).toSafeString())
        assertEquals("42", (42 as Any).toSafeString())
        assertEquals("", null.toSafeString())
    }
}
