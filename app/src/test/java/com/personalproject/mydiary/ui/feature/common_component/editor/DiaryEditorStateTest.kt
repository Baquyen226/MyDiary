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
    fun testBackspaceRemovesPrecedingDivider() {
        val state = createEditorState("Top")
        state.blocks.add(EditorBlock.Divider())
        val bottomBlock = EditorBlock.Text(RichTextState().apply { setText("Bottom") })
        state.blocks.add(bottomBlock)
        bottomBlock.state.selection = TextRange.Zero

        assertEquals(3, state.blocks.size)

        state.handleBackspaceAtStart(bottomBlock)

        assertEquals(2, state.blocks.size)
        assertTrue(state.blocks[0] is EditorBlock.Text)
        assertTrue(state.blocks[1] is EditorBlock.Text)
        assertFalse(state.blocks.any { it is EditorBlock.Divider })
    }
}
