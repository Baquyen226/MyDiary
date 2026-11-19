package com.personalproject.mydiary

import com.personalproject.mydiary.core.util.normalizeContentForHtml
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testNewlinePreservation() {
        val input = "Line 1\n\nLine 2\nLine 3"
        val html = normalizeContentForHtml(input)
        assertTrue(html.contains("<br>"))
    }
}
