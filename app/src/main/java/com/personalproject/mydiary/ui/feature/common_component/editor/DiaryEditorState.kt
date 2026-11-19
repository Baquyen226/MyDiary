package com.personalproject.mydiary.ui.feature.common_component.editor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.personalproject.mydiary.core.util.parseDiaryDocument
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import java.util.UUID

/**
 * Represents an individual block within the diary editor document.
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

    class Divider(
        override val id: String = UUID.randomUUID().toString()
    ) : EditorBlock(id)
}

/**
 * State holder for the Diary Editor, coordinating document blocks, active selection,
 * cursor-relative insertions, cross-block Backspace, multi-block selection ("Select All"),
 * and media lifecycle management.
 */
class DiaryEditorState(
    initialTitle: String = "",
    val fallbackRichTextState: RichTextState,
    initialShowFormattingBar: Boolean = false,
    initialSelectedImageUris: List<String> = emptyList(),
    var onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    var onDiscard: (() -> Unit)? = null
) {
    var title by mutableStateOf(initialTitle)
    var showFormattingBar by mutableStateOf(initialShowFormattingBar)

    val blocks = mutableStateListOf<EditorBlock>().apply {
        val firstBlock = EditorBlock.Text(fallbackRichTextState)
        add(firstBlock)
        initialSelectedImageUris.forEach { uri ->
            add(EditorBlock.Image(uri))
        }
    }

    var activeBlockId by mutableStateOf<String?>(blocks.firstOrNull()?.id)
    var focusTargetId by mutableStateOf<String?>(null)

    // Tracks whole-document selection mode (when "Select All" escalates across all blocks)
    var isDocumentSelected by mutableStateOf(false)

    // Tracks images copied to internal storage in this editing session for cleanup if uncommitted
    private val sessionCopiedImages = mutableSetOf<String>()

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
                is EditorBlock.Divider -> false
            }
        }
    }

    /**
     * Checks if the active text block has all of its text highlighted.
     */
    fun isCurrentBlockFullySelected(): Boolean {
        val activeTextBlock = blocks.filterIsInstance<EditorBlock.Text>().find { it.id == activeBlockId }
            ?: return false
        val textLength = activeTextBlock.state.toText().length
        val selection = activeTextBlock.state.selection
        if (textLength == 0) return true
        return selection.min == 0 && selection.max >= textLength
    }

    /**
     * Escalates selection to the entire document across all blocks.
     */
    fun selectAllBlocks() {
        isDocumentSelected = true
    }

    fun clearDocumentSelection() {
        isDocumentSelected = false
    }

    /**
     * Copies all document text and media references to the system clipboard.
     */
    fun copyAllToClipboard(context: Context) {
        val plainText = buildString {
            if (title.isNotBlank()) appendLine(title)
            blocks.forEach { block ->
                when (block) {
                    is EditorBlock.Text -> {
                        val text = block.state.toText().trim()
                        if (text.isNotEmpty()) appendLine(text)
                    }
                    is EditorBlock.Divider -> appendLine("────────────────────────────")
                    is EditorBlock.Image -> appendLine("[Image: ${block.uriString}]")
                }
            }
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Diary Entry", plainText.trimEnd()))
    }

    /**
     * Cuts all content to clipboard and resets the editor to a blank state.
     */
    fun cutAllToClipboard(context: Context) {
        copyAllToClipboard(context)
        deleteSelectedBlocks()
    }

    /**
     * Deletes all blocks and resets document to a single empty text block.
     */
    fun deleteSelectedBlocks() {
        cleanupSessionImages()
        blocks.clear()
        val freshBlock = EditorBlock.Text(RichTextState())
        blocks.add(freshBlock)
        activeBlockId = freshBlock.id
        focusTargetId = freshBlock.id
        isDocumentSelected = false
    }

    /**
     * Moves cursor cleanly to the end of the content when clicking empty canvas space below blocks.
     * Does NOT inject fake newline characters into user text!
     */
    fun onBlankAreaClicked() {
        if (isDocumentSelected) {
            clearDocumentSelection()
            return
        }

        val lastBlock = blocks.lastOrNull()
        if (lastBlock is EditorBlock.Text) {
            val length = lastBlock.state.toText().length
            lastBlock.state.selection = TextRange(length)
            activeBlockId = lastBlock.id
            focusTargetId = lastBlock.id
        } else {
            // If last block is an Image or Divider, append a new text block
            val newBlock = EditorBlock.Text(RichTextState())
            blocks.add(newBlock)
            activeBlockId = newBlock.id
            focusTargetId = newBlock.id
        }
    }

    /**
     * Inserts a horizontal divider cleanly after the active text block or cursor,
     * immediately placing a new text block below it for continued typing.
     */
    fun insertDividerAfterCursor() {
        clearDocumentSelection()
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
        if (fullText.isEmpty()) {
            blocks.removeAt(activeIndex)
            insertIndex = activeIndex
            blocks.add(insertIndex++, EditorBlock.Divider())
            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        } else if (cursor in 1 until fullText.length) {
            val textBefore = fullText.substring(0, cursor).trimEnd('\n')
            val textAfter = fullText.substring(cursor).trimStart('\n')
            activeTextBlock.state.setText(textBefore)

            blocks.add(insertIndex++, EditorBlock.Divider())

            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    setText(textAfter)
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        } else {
            val trimmed = fullText.trimEnd('\n')
            if (trimmed != fullText) {
                activeTextBlock.state.setText(trimmed)
            }
            blocks.add(insertIndex++, EditorBlock.Divider())
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
     * Inserts one or more images right after the cursor or active text block.
     * Preserves formatting and tracks copied files for cleanup.
     */
    fun insertImagesAfterCursor(uris: List<String>) {
        if (uris.isEmpty()) return
        clearDocumentSelection()

        uris.forEach { sessionCopiedImages.add(it) }

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
        if (fullText.isEmpty()) {
            blocks.removeAt(activeIndex)
            insertIndex = activeIndex
            for (uri in uris) {
                blocks.add(insertIndex++, EditorBlock.Image(uri))
            }
            nextTextBlock = EditorBlock.Text(
                state = RichTextState().apply {
                    selection = TextRange(0)
                }
            )
            blocks.add(insertIndex, nextTextBlock)
        } else if (cursor in 1 until fullText.length) {
            val textBefore = fullText.substring(0, cursor).trimEnd('\n')
            val textAfter = fullText.substring(cursor).trimStart('\n')
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
        } else {
            val trimmed = fullText.trimEnd('\n')
            if (trimmed != fullText) {
                activeTextBlock.state.setText(trimmed)
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
     * Handles Backspace when cursor is at index 0 of a text block.
     * Deletes the preceding non-text block (Divider/Image), or merges with preceding text block.
     */
    fun handleBackspaceAtStart(currentBlock: EditorBlock.Text) {
        val currentIndex = blocks.indexOf(currentBlock)
        if (currentIndex <= 0) return

        val prevBlock = blocks[currentIndex - 1]
        when (prevBlock) {
            is EditorBlock.Divider -> {
                blocks.removeAt(currentIndex - 1)
                activeBlockId = currentBlock.id
                focusTargetId = currentBlock.id
            }
            is EditorBlock.Image -> {
                removeBlock(prevBlock)
                activeBlockId = currentBlock.id
                focusTargetId = currentBlock.id
            }
            is EditorBlock.Text -> {
                val currentText = currentBlock.state.toText()
                if (currentText.isEmpty()) {
                    // Current block is blank: simply remove it and focus end of previous block
                    val prevLength = prevBlock.state.toText().length
                    blocks.removeAt(currentIndex)
                    prevBlock.state.selection = TextRange(prevLength)
                    activeBlockId = prevBlock.id
                    focusTargetId = prevBlock.id
                } else {
                    // Merge current block into previous block
                    val prevLength = prevBlock.state.toText().length
                    val prevHtml = prevBlock.state.toHtml()
                    val currentHtml = currentBlock.state.toHtml()
                    blocks.removeAt(currentIndex)
                    prevBlock.state.setHtml(prevHtml + currentHtml)
                    prevBlock.state.selection = TextRange(prevLength)
                    activeBlockId = prevBlock.id
                    focusTargetId = prevBlock.id
                }
            }
        }
    }

    fun removeBlock(block: EditorBlock) {
        if (block is EditorBlock.Image) {
            if (sessionCopiedImages.remove(block.uriString)) {
                deleteInternalMediaFile(block.uriString)
            }
        }
        blocks.remove(block)
        if (blocks.filterIsInstance<EditorBlock.Text>().isEmpty()) {
            val fallback = EditorBlock.Text(RichTextState())
            blocks.add(fallback)
            activeBlockId = fallback.id
            focusTargetId = fallback.id
        }
    }

    /**
     * Saves the current entry and resets the editor state.
     * Checks plain text and non-text blocks so empty <p></p> tags do not save.
     */
    fun save() {
        val htmlBuilder = StringBuilder()
        val allImages = mutableListOf<String>()

        var hasValidContent = false

        for (block in blocks) {
            when (block) {
                is EditorBlock.Text -> {
                    val plain = block.state.toText().trim()
                    if (plain.isNotEmpty()) {
                        hasValidContent = true
                        htmlBuilder.append(block.state.toHtml())
                    }
                }
                is EditorBlock.Image -> {
                    hasValidContent = true
                    allImages.add(block.uriString)
                    htmlBuilder.append("""<p><img src="${block.uriString}" /></p>""")
                }
                is EditorBlock.Divider -> {
                    hasValidContent = true
                    htmlBuilder.append("<hr />")
                }
            }
        }

        val contentHtml = htmlBuilder.toString()
        if (title.isNotBlank() || hasValidContent) {
            val savedTitle = title
            val savedImages = allImages
            // Committed: clear session set so saved files are preserved on disk
            sessionCopiedImages.clear()
            reset()
            onSave?.invoke(savedTitle, contentHtml, savedImages)
        }
    }

    fun discard() {
        cleanupSessionImages()
        reset()
        onDiscard?.invoke()
    }

    /**
     * Loads an existing diary entry into the editor document blocks.
     */
    fun loadEntry(entry: DiaryEntry) {
        loadContent(
            newTitle = entry.title,
            rawContent = entry.body.ifEmpty { entry.bodyPlain },
            imageUris = entry.imageUris
        )
    }

    /**
     * Populates the editor with specific title, raw content (HTML or plain text), and media URIs.
     */
    fun loadContent(newTitle: String, rawContent: String, imageUris: List<String>) {
        cleanupSessionImages()
        blocks.clear()
        fallbackRichTextState.clear()
        title = newTitle

        val parsedBlocks = parseDiaryDocument(rawContent, imageUris)
        if (parsedBlocks.isEmpty()) {
            val initialBlock = EditorBlock.Text(fallbackRichTextState)
            blocks.add(initialBlock)
        } else {
            blocks.addAll(parsedBlocks)
        }

        activeBlockId = blocks.firstOrNull()?.id
        focusTargetId = blocks.filterIsInstance<EditorBlock.Text>().firstOrNull()?.id
        showFormattingBar = true
        isDocumentSelected = false
    }

    fun reset() {
        title = ""
        cleanupSessionImages()
        blocks.clear()
        fallbackRichTextState.clear()
        val initialBlock = EditorBlock.Text(fallbackRichTextState)
        blocks.add(initialBlock)
        activeBlockId = initialBlock.id
        focusTargetId = null
        showFormattingBar = true
        isDocumentSelected = false
    }

    private fun cleanupSessionImages() {
        sessionCopiedImages.forEach { uriStr ->
            deleteInternalMediaFile(uriStr)
        }
        sessionCopiedImages.clear()
    }
}

@Composable
fun rememberDiaryEditorState(
    initialTitle: String = "",
    richTextState: RichTextState = rememberRichTextState(),
    initialShowFormattingBar: Boolean = false,
    initialSelectedImageUris: List<String> = emptyList(),
    onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    onDiscard: (() -> Unit)? = null
): DiaryEditorState {
    return remember {
        DiaryEditorState(
            initialTitle = initialTitle,
            fallbackRichTextState = richTextState,
            initialShowFormattingBar = initialShowFormattingBar,
            initialSelectedImageUris = initialSelectedImageUris,
            onSave = onSave,
            onDiscard = onDiscard
        )
    }.apply {
        this.onSave = onSave
        this.onDiscard = onDiscard
    }
}
