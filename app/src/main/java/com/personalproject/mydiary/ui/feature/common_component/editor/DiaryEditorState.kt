package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import java.util.UUID

/**
 * Represents an individual block within the diary editor document (either rich text or an inline image).
 */
sealed class EditorBlock(open val id: String) {
    class Text(
        val state: RichTextState,
        override val id: String = UUID.randomUUID().toString(),
        val focusRequester: FocusRequester = FocusRequester()
    ) : EditorBlock(id)

    class Image(
        val uriString: String,
        override val id: String = UUID.randomUUID().toString()
    ) : EditorBlock(id)
}

/**
 * State holder for the Diary Editor, coordinating document blocks, active selection,
 * cursor-relative image insertion, blank-line navigation, and saving/discarding.
 */
class DiaryEditorState(
    initialTitle: String = "",
    val fallbackRichTextState: RichTextState,
    initialShowFormattingBar: Boolean = false,
    initialSelectedImageUris: List<String> = emptyList(),
    initialIsRecordingVoice: Boolean = false,
    var onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    var onDiscard: (() -> Unit)? = null
) {
    var title by mutableStateOf(initialTitle)
    var showFormattingBar by mutableStateOf(initialShowFormattingBar)
    var isRecordingVoice by mutableStateOf(initialIsRecordingVoice)

    val blocks = mutableStateListOf<EditorBlock>().apply {
        val firstBlock = EditorBlock.Text(fallbackRichTextState)
        add(firstBlock)
        initialSelectedImageUris.forEach { uri ->
            add(EditorBlock.Image(uri))
        }
    }

    var activeBlockId by mutableStateOf<String?>(blocks.firstOrNull()?.id)
    var focusTargetId by mutableStateOf<String?>(null)

    val activeRichTextState: RichTextState
        get() {
            val activeBlock = blocks.filterIsInstance<EditorBlock.Text>().find { it.id == activeBlockId }
            return activeBlock?.state ?: blocks.filterIsInstance<EditorBlock.Text>().lastOrNull()?.state ?: fallbackRichTextState
        }

    val selectedImageUris: List<String>
        get() = blocks.filterIsInstance<EditorBlock.Image>().map { it.uriString }

    fun isEmpty(): Boolean {
        return title.isBlank() && blocks.all {
            when (it) {
                is EditorBlock.Text -> it.state.toText().isBlank()
                is EditorBlock.Image -> false
            }
        }
    }

    /**
     * Inserts one or more images right after and below the cursor in the active text block.
     * If the cursor is mid-text, the block splits: text before stays, image is inserted,
     * remaining text goes after the image, and cursor moves to after the image.
     */
    fun insertImagesAfterCursor(uris: List<String>) {
        if (uris.isEmpty()) return

        val activeTextBlock = blocks.filterIsInstance<EditorBlock.Text>().find { it.id == activeBlockId }
            ?: blocks.filterIsInstance<EditorBlock.Text>().lastOrNull()
            ?: run {
                val newBlock = EditorBlock.Text(RichTextState())
                blocks.add(newBlock)
                newBlock
            }

        val activeIndex = blocks.indexOf(activeTextBlock).coerceAtLeast(0)
        val fullText = activeTextBlock.state.toText()
        val cursor = activeTextBlock.state.selection.max.coerceIn(0, fullText.length)

        var insertIndex = activeIndex + 1

        val nextTextBlock: EditorBlock.Text
        if (cursor in 1 until fullText.length) {
            // Split text at cursor
            val textBefore = fullText.substring(0, cursor)
            val textAfter = fullText.substring(cursor)
            activeTextBlock.state.setText(textBefore)

            for (uri in uris) {
                blocks.add(insertIndex++, EditorBlock.Image(uri))
            }

            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    setText(textAfter)
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        } else if (cursor == 0 && fullText.isNotEmpty()) {
            // Image inserted before existing text
            activeTextBlock.state.setText("")

            for (uri in uris) {
                blocks.add(insertIndex++, EditorBlock.Image(uri))
            }

            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    setText(fullText)
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        } else {
            // Cursor at end of block
            if (fullText.isEmpty() && blocks.size == 1) {
                blocks.removeAt(activeIndex)
                insertIndex = activeIndex
            }

            for (uri in uris) {
                blocks.add(insertIndex++, EditorBlock.Image(uri))
            }

            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        }

        activeBlockId = nextTextBlock.id
        focusTargetId = nextTextBlock.id
    }

    /**
     * Moves cursor to a blank line in the blank space below the text.
     * Calculates the number of lines based on vertical tap offset and appends newlines
     * to the last text block so the user can start typing at that exact blank line.
     */
    fun moveToBlankLine(offsetY: Float, density: Density) {
        val lineHeightPx = with(density) { 24.sp.toPx() }
        val linesToAdd = (offsetY / lineHeightPx).toInt().coerceIn(1, 40)

        val lastBlock = blocks.lastOrNull()
        if (lastBlock is EditorBlock.Text) {
            val currentText = lastBlock.state.toText()
            val newText = currentText + "\n".repeat(linesToAdd)
            lastBlock.state.setText(newText)
            lastBlock.state.selection = TextRange(newText.length)
            activeBlockId = lastBlock.id
            focusTargetId = lastBlock.id
        } else {
            val newBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    val initialText = if (linesToAdd > 1) "\n".repeat(linesToAdd - 1) else ""
                    if (initialText.isNotEmpty()) {
                        setText(initialText)
                        selection = TextRange(initialText.length)
                    }
                }
            )
            blocks.add(newBlock)
            activeBlockId = newBlock.id
            focusTargetId = newBlock.id
        }
    }

    fun removeBlock(block: EditorBlock) {
        blocks.remove(block)
        if (blocks.filterIsInstance<EditorBlock.Text>().isEmpty()) {
            val fallback = EditorBlock.Text(RichTextState())
            blocks.add(fallback)
            activeBlockId = fallback.id
        }
    }

    /**
     * Saves the current entry and resets the editor state.
     */
    fun save() {
        val htmlBuilder = StringBuilder()
        val allImages = mutableListOf<String>()

        for (block in blocks) {
            when (block) {
                is EditorBlock.Text -> {
                    val html = block.state.toHtml()
                    val plain = block.state.toText().trim()
                    if (plain.isNotEmpty()) {
                        htmlBuilder.append(html)
                    }
                }
                is EditorBlock.Image -> {
                    allImages.add(block.uriString)
                    htmlBuilder.append("""<p><img src="${block.uriString}" /></p>""")
                }
            }
        }

        val contentHtml = htmlBuilder.toString()
        if (title.isNotBlank() || contentHtml.isNotBlank() || allImages.isNotEmpty()) {
            val savedTitle = title
            val savedImages = allImages
            reset()
            onSave?.invoke(savedTitle, contentHtml, savedImages)
        }
    }

    fun discard() {
        reset()
        onDiscard?.invoke()
    }

    fun reset() {
        title = ""
        blocks.clear()
        fallbackRichTextState.clear()
        val initialBlock = EditorBlock.Text(fallbackRichTextState)
        blocks.add(initialBlock)
        activeBlockId = initialBlock.id
        focusTargetId = null
        isRecordingVoice = false
        showFormattingBar = false
    }
}

@Composable
fun rememberDiaryEditorState(
    initialTitle: String = "",
    richTextState: RichTextState = rememberRichTextState(),
    initialShowFormattingBar: Boolean = false,
    initialSelectedImageUris: List<String> = emptyList(),
    initialIsRecordingVoice: Boolean = false,
    onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    onDiscard: (() -> Unit)? = null
): DiaryEditorState {
    return remember {
        DiaryEditorState(
            initialTitle = initialTitle,
            fallbackRichTextState = richTextState,
            initialShowFormattingBar = initialShowFormattingBar,
            initialSelectedImageUris = initialSelectedImageUris,
            initialIsRecordingVoice = initialIsRecordingVoice,
            onSave = onSave,
            onDiscard = onDiscard
        )
    }.apply {
        this.onSave = onSave
        this.onDiscard = onDiscard
    }
}
