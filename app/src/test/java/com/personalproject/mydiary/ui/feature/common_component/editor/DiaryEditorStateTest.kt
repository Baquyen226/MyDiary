package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.compose.ui.text.TextRange
import com.mohamedrejeb.richeditor.model.RichTextState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiaryEditorStateTest {

    private fun createEditorState(initialText: String = ""): DiaryEditorState {
        val richTextState = RichTextState().apply {
            if (initialText.isNotEmpty()) {
                setText(initialText)
            }
        }
        return DiaryEditorState(fallbackRichTextState = richTextState)
    }

    @Test
    fun testInsertDividerAtEmptyBlock() {
        val state = createEditorState("")
        assertEquals(1, state.blocks.size)
        assertTrue(state.blocks[0] is EditorBlock.Text)

        state.insertDividerAfterCursor()

        // Empty block replaced by Divider + fresh empty Text block
        assertEquals(2, state.blocks.size)
        assertTrue(state.blocks[0] is EditorBlock.Divider)
        assertTrue(state.blocks[1] is EditorBlock.Text)
    }

    @Test
    fun testInsertDividerAtEndOfText() {
        val state = createEditorState("Hello World")
        val textBlock = state.blocks[0] as EditorBlock.Text
        textBlock.state.selection = TextRange("Hello World".length)

        state.insertDividerAfterCursor()

        assertEquals(3, state.blocks.size)
        assertEquals("Hello World", (state.blocks[0] as EditorBlock.Text).state.toText())
        assertTrue(state.blocks[1] is EditorBlock.Divider)
        assertTrue(state.blocks[2] is EditorBlock.Text)
        assertEquals("", (state.blocks[2] as EditorBlock.Text).state.toText())
    }

    @Test
    fun testInsertDividerMidTextSplitsBlock() {
        val state = createEditorState("HelloWorld")
        val textBlock = state.blocks[0] as EditorBlock.Text
        // Cursor between "Hello" and "World" (index 5)
        textBlock.state.selection = TextRange(5)

        state.insertDividerAfterCursor()

        assertEquals(3, state.blocks.size)
        assertEquals("Hello", (state.blocks[0] as EditorBlock.Text).state.toText())
        assertTrue(state.blocks[1] is EditorBlock.Divider)
        assertEquals("World", (state.blocks[2] as EditorBlock.Text).state.toText())
    }

    @Test
    fun testShiftSelectedBlocksUpAndDown() {
        val state = createEditorState("Block 0")
        state.insertDividerAfterCursor() // Block 1 = Divider, Block 2 = Text
        val textBlock2 = state.blocks[2] as EditorBlock.Text
        textBlock2.state.setText("Block 2")

        assertEquals(3, state.blocks.size)

        // Select Block 2 (index 2)
        state.selectedRange = 2..2

        // Move up -> shifts to index 1
        state.moveSelectedBlocksUp()
        assertEquals(1..1, state.selectedRange)
        assertEquals("Block 2", (state.blocks[1] as EditorBlock.Text).state.toText())
        assertTrue(state.blocks[2] is EditorBlock.Divider)

        // Move up again -> shifts to index 0
        state.moveSelectedBlocksUp()
        assertEquals(0..0, state.selectedRange)
        assertEquals("Block 2", (state.blocks[0] as EditorBlock.Text).state.toText())

        // Move up when at top should be a no-op
        state.moveSelectedBlocksUp()
        assertEquals(0..0, state.selectedRange)

        // Move down -> shifts to index 1
        state.moveSelectedBlocksDown()
        assertEquals(1..1, state.selectedRange)
        assertEquals("Block 2", (state.blocks[1] as EditorBlock.Text).state.toText())
    }

    @Test
    fun testBackspaceMergeJoinsLinesInSingleParagraph() {
        val state = createEditorState("First")
        val secondBlock = EditorBlock.Text(RichTextState().apply { setText("Second") })
        state.blocks.add(secondBlock)
        secondBlock.state.selection = TextRange.Zero

        assertEquals(2, state.blocks.size)

        // Backspace at index 0 of secondBlock merges it into firstBlock
        state.handleBackspaceAtStart(secondBlock)

        assertEquals(1, state.blocks.size)
        val mergedText = (state.blocks[0] as EditorBlock.Text).state.toText()
        assertEquals("FirstSecond", mergedText)
    }

    @Test
    fun testBackspaceRemovesPrecedingDividerAndMergesAdjacentText() {
        val state = createEditorState("Top")
        state.blocks.add(EditorBlock.Divider())
        val bottomBlock = EditorBlock.Text(RichTextState().apply { setText("Bottom") })
        state.blocks.add(bottomBlock)
        bottomBlock.state.selection = TextRange.Zero

        assertEquals(3, state.blocks.size)

        state.handleBackspaceAtStart(bottomBlock)

        // Divider is removed and adjacent text blocks (Top + Bottom) merge into 1
        assertEquals(1, state.blocks.size)
        assertTrue(state.blocks[0] is EditorBlock.Text)
        assertEquals("TopBottom", (state.blocks[0] as EditorBlock.Text).state.toText())
        assertFalse(state.blocks.any { it is EditorBlock.Divider })
    }

    @Test
    fun testRemoveBlockMergesAdjacentTextBlocks() {
        val state = createEditorState("Header")
        val imageBlock = EditorBlock.Image("test://image.png")
        val bodyBlock = EditorBlock.Text(RichTextState().apply { setText("Body") })
        state.blocks.add(imageBlock)
        state.blocks.add(bodyBlock)

        assertEquals(3, state.blocks.size)

        state.removeBlock(imageBlock)

        // Removing the middle image merges "Header" and "Body" into 1 text block
        assertEquals(1, state.blocks.size)
        assertEquals("HeaderBody", (state.blocks[0] as EditorBlock.Text).state.toText())
    }

    @Test
    fun testInsertBlocksAtCursorClearsBlockSelection() {
        val state = createEditorState("Line 1")
        val line2 = EditorBlock.Text(RichTextState().apply { setText("Line 2") })
        state.blocks.add(line2)

        // Select all blocks
        state.selectAllBlocks()
        assertTrue(state.hasBlockSelection)

        // Insert divider
        state.insertDividerAfterCursor()

        // Block selection must be fully cleared (leak prevented)
        assertFalse(state.hasBlockSelection)
        assertEquals(null, state.selectedRange)
    }

    @Test
    fun testBackspaceMergeWithListsPreservesListStructure() {
        val state = createEditorState("")
        val listBlock = EditorBlock.Text(RichTextState().apply {
            setHtml("<ul><li>Item 1</li><li>Item 2</li></ul>")
        })
        val textBlock = EditorBlock.Text(RichTextState().apply {
            setHtml("<p>Paragraph</p>")
        })
        state.blocks.clear()
        state.blocks.add(listBlock)
        state.blocks.add(textBlock)

        textBlock.state.selection = TextRange.Zero
        state.handleBackspaceAtStart(textBlock)

        assertEquals(1, state.blocks.size)
        val mergedHtml = (state.blocks[0] as EditorBlock.Text).state.toHtml()
        assertTrue(mergedHtml.contains("<ul>"))
        assertTrue(mergedHtml.contains("<li>Item 1</li>"))
        assertTrue(mergedHtml.contains("Paragraph"))
    }

    @Test
    fun testParseDiaryDocumentPreservesListTagsAcrossDividers() {
        val html = """
            <ul>
                <li>Item 1</li><hr>
                <li>Item 2</li><hr>
                <li>Item 3</li>
            </ul>
        """.trimIndent()

        val blocks = com.personalproject.mydiary.core.util.parseDiaryDocument(html)
        assertEquals(5, blocks.size)
        assertTrue(blocks[0] is EditorBlock.Text)
        assertTrue(blocks[1] is EditorBlock.Divider)
        assertTrue(blocks[2] is EditorBlock.Text)
        assertTrue(blocks[3] is EditorBlock.Divider)
        assertTrue(blocks[4] is EditorBlock.Text)

        val html0 = (blocks[0] as EditorBlock.Text).state.toHtml()
        val html2 = (blocks[2] as EditorBlock.Text).state.toHtml()
        val html4 = (blocks[4] as EditorBlock.Text).state.toHtml()

        assertTrue("Block 0 must contain <ul>: $html0", html0.contains("<ul>") || html0.contains("<ul "))
        assertTrue("Block 2 must contain <ul>: $html2", html2.contains("<ul>") || html2.contains("<ul "))
        assertTrue("Block 4 must contain <ul>: $html4", html4.contains("<ul>") || html4.contains("<ul "))
    }

    @Test
    fun testNormalizeContentForHtmlDoesNotAddExtraNewlinesAroundLists() {
        val input = """
            <ul>
                <li>Item 1</li>
                <li>Item 2</li>
                <li>Item 3</li>
            </ul>
        """.trimIndent()

        val normalized = com.personalproject.mydiary.core.util.normalizeContentForHtml(input)
        assertFalse("Must not have <br> after <ul>: $normalized", normalized.contains("<ul><br>") || normalized.contains("<ul>\n"))
        assertFalse("Must not have <br> before </ul>: $normalized", normalized.contains("<br></ul>") || normalized.contains("\n</ul>"))
        assertEquals("<ul><li>Item 1</li><li>Item 2</li><li>Item 3</li></ul>", normalized)
    }
}
