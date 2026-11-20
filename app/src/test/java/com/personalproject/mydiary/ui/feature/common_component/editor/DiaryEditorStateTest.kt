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

    @Test
    fun testApplyOrderedListToMultipleBlocksAcrossDividers() {
        val state = createEditorState()
        state.loadContent(
            newTitle = "",
            rawContent = "Line 1<hr>Line 2<hr>Line 3<hr>",
            imageUris = emptyList()
        )

        // Ensure blocks are properly loaded as alternating Text and Divider
        assertEquals(6, state.blocks.size)
        assertTrue(state.blocks[0] is EditorBlock.Text)
        assertTrue(state.blocks[1] is EditorBlock.Divider)
        assertTrue(state.blocks[2] is EditorBlock.Text)
        assertTrue(state.blocks[3] is EditorBlock.Divider)
        assertTrue(state.blocks[4] is EditorBlock.Text)
        assertTrue(state.blocks[5] is EditorBlock.Divider)

        // Select all blocks across the document
        state.selectAllBlocks()
        assertTrue(state.hasBlockSelection)

        // Apply ordered list formatting
        state.toggleOrderedList()

        val block0 = state.blocks[0] as EditorBlock.Text
        val block2 = state.blocks[2] as EditorBlock.Text
        val block4 = state.blocks[4] as EditorBlock.Text

        assertTrue(block0.state.isOrderedList)
        assertTrue(block2.state.isOrderedList)
        assertTrue(block4.state.isOrderedList)

        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("2. Line 2", block2.state.toText().trim())
        assertEquals("3. Line 3", block4.state.toText().trim())

        val combinedResult = state.blocks.joinToString("") { block ->
            when (block) {
                is EditorBlock.Text -> block.state.toText().trim()
                is EditorBlock.Divider -> "<hr>"
                is EditorBlock.Image -> ""
            }
        }
        assertEquals("1. Line 1<hr>2. Line 2<hr>3. Line 3<hr>", combinedResult)

        var savedHtml = ""
        state.onSave = { _, html, _ -> savedHtml = html }
        state.save()
        assertTrue("Saved HTML must contain start='2': $savedHtml", savedHtml.contains("start=\"2\""))
        assertTrue("Saved HTML must contain start='3': $savedHtml", savedHtml.contains("start=\"3\""))
    }

    @Test
    fun testUnapplyAndReapplyOrderedListToSecondBlock() {
        val state = createEditorState()
        state.loadContent(
            newTitle = "",
            rawContent = "Line 1<hr>Line 2<hr>Line 3<hr>",
            imageUris = emptyList()
        )

        // Select all blocks across the document and apply ordered list
        state.selectAllBlocks()
        state.toggleOrderedList()

        val block0 = state.blocks[0] as EditorBlock.Text
        val block2 = state.blocks[2] as EditorBlock.Text
        val block4 = state.blocks[4] as EditorBlock.Text

        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("2. Line 2", block2.state.toText().trim())
        assertEquals("3. Line 3", block4.state.toText().trim())

        // 1. Unapply ordered list to the second block (index 2)
        state.selectBlock(2)
        state.toggleOrderedList()

        assertFalse("Block 2 should no longer be an ordered list", block2.state.isOrderedList)
        assertEquals("Line 2", block2.state.toText().trim())
        assertEquals("1. Line 1", block0.state.toText().trim())

        // 2. Reapply ordered list to the second block (index 2)
        state.toggleOrderedList()

        assertTrue("Block 2 should be an ordered list again", block2.state.isOrderedList)
        assertEquals("2. Line 2", block2.state.toText().trim())
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("3. Line 3", block4.state.toText().trim())

        val combinedResult = state.blocks.joinToString("") { block ->
            when (block) {
                is EditorBlock.Text -> block.state.toText().trim()
                is EditorBlock.Divider -> "<hr>"
                is EditorBlock.Image -> ""
            }
        }
        assertEquals("1. Line 1<hr>2. Line 2<hr>3. Line 3<hr>", combinedResult)
    }

    @Test
    fun testApplyUnorderedListToMultiLineBlockText2() {
        val state = createEditorState()
        val raw = "Line 1<hr>Line 2<br>Line 3<hr>Line 4<br>Line 5"
        state.loadContent(newTitle = "", rawContent = raw, imageUris = emptyList())

        // Select all blocks across the document and apply ordered list
        state.selectAllBlocks()
        state.toggleOrderedList()

        val block0 = state.blocks[0] as EditorBlock.Text
        val block2 = state.blocks[2] as EditorBlock.Text
        val block4 = state.blocks[4] as EditorBlock.Text

        // Verify initial 1..5 structure across lines and dividers
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("2. Line 2\n3. Line 3", block2.state.toText().trim())
        assertEquals("4. Line 4\n5. Line 5", block4.state.toText().trim())

        // Place cursor on text 2 of block 2 ("Line 3") and apply unordered list
        val line3Idx = block2.state.annotatedString.text.indexOf("Line 3")
        block2.state.selection = TextRange(line3Idx + 1)
        state.activeBlockId = block2.id
        state.clearBlockSelection()
        state.toggleUnorderedList()

        // Block 2 line 1 remains ordered (2. Line 2), line 2 becomes unordered (• Line 3)
        assertEquals("2. Line 2\n• Line 3", block2.state.toText().trim())
        assertTrue(block2.state.toHtml().contains("<ol start=\"2\"><li>Line 2</li></ol>"))
        assertTrue(block2.state.toHtml().contains("<ul><li>Line 3</li></ul>"))

        // Block 0 retains 1. Line 1
        assertEquals("1. Line 1", block0.state.toText().trim())

        // Block 4 sequence restarts because ordered list sequence was interrupted by unordered list
        assertEquals("1. Line 4\n2. Line 5", block4.state.toText().trim())

        // Reapply ordered list to text 2 of block 2 ("Line 3")
        block2.state.selection = TextRange(line3Idx + 1)
        state.toggleOrderedList()

        // Block 2 is restored to 2. Line 2 \n 3. Line 3
        assertEquals("2. Line 2\n3. Line 3", block2.state.toText().trim())
        assertTrue(block2.state.toHtml().contains("<ol start=\"2\"><li>Line 2</li><li>Line 3</li></ol>"))

        // Block 4 resynchronizes to 4. Line 4 \n 5. Line 5
        assertEquals("4. Line 4\n5. Line 5", block4.state.toText().trim())
        assertTrue(block4.state.toHtml().contains("<ol start=\"4\"><li>Line 4</li><li>Line 5</li></ol>"))
    }

    @Test
    fun testUnapplyAndReapplyOrderedListToMultiLineBlock() {
        val state = createEditorState()
        val raw = "Line 1<hr>Line 2<br>Line 3<hr>Line 4<br>Line 5"
        state.loadContent(newTitle = "", rawContent = raw, imageUris = emptyList())

        // Select all blocks across the document and apply ordered list
        state.selectAllBlocks()
        state.toggleOrderedList()

        val block0 = state.blocks[0] as EditorBlock.Text
        val block2 = state.blocks[2] as EditorBlock.Text
        val block4 = state.blocks[4] as EditorBlock.Text

        // Verify initial 1..5 structure across lines and dividers
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("2. Line 2\n3. Line 3", block2.state.toText().trim())
        assertEquals("4. Line 4\n5. Line 5", block4.state.toText().trim())

        // 1. Unapply ordered list to block 2 (index 2)
        state.selectBlock(2)
        state.toggleOrderedList()

        assertFalse("Block 2 should no longer be an ordered list", block2.state.isOrderedList)
        assertEquals("Line 2\nLine 3", block2.state.toText().trim())
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("1. Line 4\n2. Line 5", block4.state.toText().trim())

        // 2. Reapply ordered list to block 2 (index 2)
        state.toggleOrderedList()

        assertTrue("Block 2 should be an ordered list again", block2.state.isOrderedList)
        assertEquals("2. Line 2\n3. Line 3", block2.state.toText().trim())
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("4. Line 4\n5. Line 5", block4.state.toText().trim())

        val combinedResult = state.blocks.joinToString("") { block ->
            when (block) {
                is EditorBlock.Text -> block.state.toText().trim()
                is EditorBlock.Divider -> "<hr>"
                is EditorBlock.Image -> ""
            }
        }
        assertEquals("1. Line 1<hr>2. Line 2\n3. Line 3<hr>4. Line 4\n5. Line 5", combinedResult)
    }

    @Test
    fun testApplyUnorderedListToSecondBlock() {
        val state = createEditorState()
        state.loadContent(
            newTitle = "",
            rawContent = "Line 1<hr>Line 2<hr>Line 3<hr>",
            imageUris = emptyList()
        )

        // Select all blocks across the document and apply ordered list
        state.selectAllBlocks()
        state.toggleOrderedList()

        val block0 = state.blocks[0] as EditorBlock.Text
        val block2 = state.blocks[2] as EditorBlock.Text
        val block4 = state.blocks[4] as EditorBlock.Text

        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("2. Line 2", block2.state.toText().trim())
        assertEquals("3. Line 3", block4.state.toText().trim())

        // Apply unordered list onto the second block (index 2)
        state.selectBlock(2)
        state.toggleUnorderedList()

        assertTrue("Block 2 should now be an unordered list", block2.state.isUnorderedList)
        assertFalse("Block 2 should no longer be an ordered list", block2.state.isOrderedList)
        assertEquals("• Line 2", block2.state.toText().trim())
        assertTrue("Block 2 HTML should contain <ul> tag", block2.state.toHtml().contains("<ul>"))

        // Block 0 retains 1. Line 1
        assertTrue(block0.state.isOrderedList)
        assertEquals("1. Line 1", block0.state.toText().trim())

        // Block 4 sequence restarts because ordered list sequence was interrupted by unordered list
        assertTrue(block4.state.isOrderedList)
        assertEquals("1. Line 3", block4.state.toText().trim())

        val combinedResult = state.blocks.joinToString("") { block ->
            when (block) {
                is EditorBlock.Text -> block.state.toText().trim()
                is EditorBlock.Divider -> "<hr>"
                is EditorBlock.Image -> ""
            }
        }
        assertEquals("1. Line 1<hr>• Line 2<hr>1. Line 3<hr>", combinedResult)

        val block2Html = block2.state.toHtml()
        assertTrue("Block 2 HTML must contain unordered list: $block2Html", block2Html.contains("<ul>") && block2Html.contains("<li>Line 2</li>"))

        // Switch the second block back to an ordered list
        state.toggleOrderedList()

        assertTrue("Block 2 should be an ordered list again", block2.state.isOrderedList)
        assertFalse("Block 2 should no longer be an unordered list", block2.state.isUnorderedList)
        assertEquals("2. Line 2", block2.state.toText().trim())
        assertEquals("1. Line 1", block0.state.toText().trim())
        assertEquals("3. Line 3", block4.state.toText().trim())

        val restoredResult = state.blocks.joinToString("") { block ->
            when (block) {
                is EditorBlock.Text -> block.state.toText().trim()
                is EditorBlock.Divider -> "<hr>"
                is EditorBlock.Image -> ""
            }
        }
        assertEquals("1. Line 1<hr>2. Line 2<hr>3. Line 3<hr>", restoredResult)

        var savedHtml = ""
        state.onSave = { _, html, _ -> savedHtml = html }
        state.save()
        assertTrue("Saved HTML must contain start='2': $savedHtml", savedHtml.contains("start=\"2\""))
        assertTrue("Saved HTML must contain start='3': $savedHtml", savedHtml.contains("start=\"3\""))
    }

    @Test
    fun testParseDiaryDocumentPreservesOrderedListSequenceAcrossDividers() {
        val html = """
            <ol>
                <li>Line 1</li><hr>
                <li>Line 2</li><hr>
                <li>Line 3</li>
            </ol>
        """.trimIndent()

        val blocks = com.personalproject.mydiary.core.util.parseDiaryDocument(html)
        assertEquals(5, blocks.size)
        assertTrue(blocks[0] is EditorBlock.Text)
        assertTrue(blocks[1] is EditorBlock.Divider)
        assertTrue(blocks[2] is EditorBlock.Text)
        assertTrue(blocks[3] is EditorBlock.Divider)
        assertTrue(blocks[4] is EditorBlock.Text)

        assertEquals("1. Line 1", (blocks[0] as EditorBlock.Text).state.toText().trim())
        assertEquals("2. Line 2", (blocks[2] as EditorBlock.Text).state.toText().trim())
        assertEquals("3. Line 3", (blocks[4] as EditorBlock.Text).state.toText().trim())
    }
}
