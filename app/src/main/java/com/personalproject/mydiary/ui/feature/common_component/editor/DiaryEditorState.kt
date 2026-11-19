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
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
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

    // Tracks contiguous range of selected blocks by index (e.g. 0..2)
    var selectedRange by mutableStateOf<IntRange?>(null)

    val isDocumentSelected: Boolean
        get() = selectedRange != null && selectedRange!!.first == 0 && selectedRange!!.last == blocks.size - 1

    val hasBlockSelection: Boolean
        get() = selectedRange != null

    fun isBlockSelected(index: Int): Boolean {
        return selectedRange?.contains(index) == true
    }

    // Tracks images originally loaded from existing entry for cleanup on save if removed
    private val initialLoadedImages = mutableSetOf<String>()

    // Tracks images copied to internal storage in this editing session for cleanup if uncommitted
    private val sessionCopiedImages = mutableSetOf<String>()

    val activeTextBlock: EditorBlock.Text?
        get() = blocks.filterIsInstance<EditorBlock.Text>().find { it.id == activeBlockId }
            ?: blocks.filterIsInstance<EditorBlock.Text>().lastOrNull()

    val currentBlockIndex: Int
        get() = blocks.indexOfFirst { it.id == activeBlockId }.takeIf { it != -1 } ?: 0

    val activeRichTextState: RichTextState
        get() = activeTextBlock?.state ?: fallbackRichTextState



    fun isEmpty(): Boolean {
        return title.isBlank() && blocks.all {
            when (it) {
                is EditorBlock.Text -> it.state.annotatedString.text.isBlank()
                is EditorBlock.Image -> false
                is EditorBlock.Divider -> false
            }
        }
    }

    /**
     * Checks if the active text block has all of its text highlighted.
     */
    fun isCurrentBlockFullySelected(): Boolean {
        val textBlock = activeTextBlock ?: return false
        val textLength = textBlock.state.annotatedString.text.length
        val selection = textBlock.state.selection
        if (textLength == 0) return true
        return selection.min == 0 && selection.max >= textLength
    }

    /**
     * Selects a specific block by its index in the blocks list.
     */
    fun selectBlock(index: Int) {
        if (index in blocks.indices) {
            activeBlockId = blocks[index].id
            selectedRange = index..index
            highlightTextBlockContent(index)
        }
    }

    /**
     * Selects only the active (focused) block.
     */
    fun selectCurrentBlock() {
        selectBlock(currentBlockIndex)
    }

    /**
     * Extends block selection upward by one block.
     */
    fun extendSelectionUp() = extendSelection(delta = -1)

    /**
     * Extends block selection downward by one block.
     */
    fun extendSelectionDown() = extendSelection(delta = 1)

    private fun extendSelection(delta: Int) {
        val current = selectedRange ?: (currentBlockIndex..currentBlockIndex)
        val newRange = if (delta < 0) {
            (current.first + delta).coerceAtLeast(0)..current.last
        } else {
            current.first..(current.last + delta).coerceAtMost(blocks.size - 1)
        }
        selectedRange = newRange
        val targetIndex = if (delta < 0) newRange.first else newRange.last
        highlightTextBlockContent(targetIndex)
    }

    /**
     * Escalates selection to the entire document across all blocks.
     */
    fun selectAllBlocks() {
        if (blocks.isNotEmpty()) {
            selectedRange = 0 until blocks.size
            blocks.filterIsInstance<EditorBlock.Text>().forEach { block ->
                val len = block.state.annotatedString.text.length
                block.state.selection = TextRange(0, len)
            }
        }
    }

    fun clearDocumentSelection() {
        clearBlockSelection()
    }

    fun clearBlockSelection() {
        selectedRange = null
        val active = activeTextBlock
        blocks.filterIsInstance<EditorBlock.Text>().forEach { block ->
            val len = block.state.annotatedString.text.length
            block.state.selection = if (block.id == active?.id) TextRange(len) else TextRange.Zero
        }
        mergeAdjacentTextBlocks()
    }

    private fun highlightTextBlockContent(index: Int) {
        val block = blocks.getOrNull(index)
        if (block is EditorBlock.Text) {
            block.state.selection = TextRange(0, block.state.annotatedString.text.length)
        }
    }

    private inline fun applyToSelectedTextBlocks(action: (RichTextState) -> Unit) {
        val range = selectedRange
        if (range != null) {
            blocks.filterIndexed { index, _ -> index in range }
                .filterIsInstance<EditorBlock.Text>()
                .forEach { block ->
                    val len = block.state.annotatedString.text.length
                    block.state.selection = TextRange(0, len)
                    action(block.state)
                }
        } else {
            action(activeRichTextState)
        }
    }

    fun toggleSpanStyle(spanStyle: SpanStyle) = applyToSelectedTextBlocks { it.toggleSpanStyle(spanStyle) }
    fun addParagraphStyle(paragraphStyle: ParagraphStyle) = applyToSelectedTextBlocks { it.addParagraphStyle(paragraphStyle) }
    fun toggleUnorderedList() = applyToSelectedTextBlocks { it.toggleUnorderedList() }
    fun toggleOrderedList() = applyToSelectedTextBlocks { it.toggleOrderedList() }

    /**
     * Copies selected document text and media references to the system clipboard.
     */
    fun copySelectedToClipboard(context: Context) {
        val range = selectedRange ?: (0 until blocks.size)
        val selectedBlocks = blocks.filterIndexed { index, _ -> index in range }
        val plainText = buildString {
            if (range.first == 0 && title.isNotBlank()) appendLine(title)
            selectedBlocks.forEach { block ->
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
     * Cuts selected content to clipboard and deletes them.
     */
    fun cutAllToClipboard(context: Context) {
        copySelectedToClipboard(context)
        deleteSelectedBlocks()
    }

    /**
     * Deletes selected blocks and resets editor if all were deleted.
     */
    fun deleteSelectedBlocks() {
        val range = selectedRange
        if (range == null || (range.first == 0 && range.last == blocks.size - 1)) {
            cleanupSessionImages()
            blocks.clear()
            val freshBlock = EditorBlock.Text(RichTextState())
            blocks.add(freshBlock)
            activeBlockId = freshBlock.id
            focusTargetId = freshBlock.id
            selectedRange = null
        } else {
            val blocksToDelete = blocks.filterIndexed { index, _ -> index in range }
            blocksToDelete.forEach { block ->
                if (block is EditorBlock.Image) {
                    if (sessionCopiedImages.remove(block.uriString)) {
                        deleteInternalMediaFile(block.uriString)
                    }
                }
            }
            blocks.removeAll(blocksToDelete)
            if (blocks.isEmpty() || blocks.none { it is EditorBlock.Text }) {
                val freshBlock = EditorBlock.Text(RichTextState())
                blocks.add(freshBlock)
                activeBlockId = freshBlock.id
                focusTargetId = freshBlock.id
            } else {
                val nextActive = blocks.getOrNull(range.first) ?: blocks.last()
                activeBlockId = nextActive.id
                focusTargetId = nextActive.id
                mergeAdjacentTextBlocks()
            }
            selectedRange = null
        }
    }



    /**
     * Moves the currently selected block range upward by one position.
     */
    fun moveSelectedBlocksUp() = shiftSelectedBlocks(offset = -1)

    /**
     * Moves the currently selected block range downward by one position.
     */
    fun moveSelectedBlocksDown() = shiftSelectedBlocks(offset = 1)

    private fun shiftSelectedBlocks(offset: Int) {
        val range = selectedRange ?: return
        val newFirst = range.first + offset
        val newLast = range.last + offset
        if (newFirst < 0 || newLast >= blocks.size) return

        val count = range.count()
        val itemsToMove = blocks.slice(range).toList()
        blocks.removeAll(itemsToMove)
        blocks.addAll(newFirst, itemsToMove)
        selectedRange = newFirst until (newFirst + count)
    }

    /**
     * Moves cursor cleanly to the end of the content when clicking empty canvas space below blocks.
     * Does NOT inject fake newline characters into user text!
     */
    fun onBlankAreaClicked() {
        if (hasBlockSelection) {
            clearBlockSelection()
            return
        }

        val lastBlock = blocks.lastOrNull()
        if (lastBlock is EditorBlock.Text) {
            val length = lastBlock.state.annotatedString.text.length
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
        insertBlocksAtCursor(listOf(EditorBlock.Divider()))
    }

    /**
     * Inserts one or more images right after the cursor or active text block.
     * Preserves formatting and tracks copied files for cleanup.
     */
    fun insertImagesAfterCursor(uris: List<String>) {
        if (uris.isEmpty()) return
        uris.forEach { sessionCopiedImages.add(it) }
        insertBlocksAtCursor(uris.map { EditorBlock.Image(it) })
    }

    private fun insertBlocksAtCursor(newBlocks: List<EditorBlock>) {
        val textBlock = activeTextBlock ?: run {
            val fresh = EditorBlock.Text(RichTextState())
            blocks.add(fresh)
            fresh
        }

        val activeIndex = blocks.indexOf(textBlock).coerceAtLeast(0)
        val fullText = textBlock.state.annotatedString.text
        // Read cursor before clearing selection
        val cursor = if (hasBlockSelection) fullText.length
                     else textBlock.state.selection.max.coerceIn(0, fullText.length)
        clearBlockSelection()

        var insertIndex = activeIndex + 1
        val nextTextBlock: EditorBlock.Text

        if (fullText.isEmpty()) {
            blocks.removeAt(activeIndex)
            insertIndex = activeIndex
            blocks.addAll(insertIndex, newBlocks)
            insertIndex += newBlocks.size
            nextTextBlock = EditorBlock.Text(RichTextState().apply { selection = TextRange.Zero })
            blocks.add(insertIndex, nextTextBlock)
        } else if (cursor in 1 until fullText.length) {
            val originalHtml = textBlock.state.toHtml()
            val nextState = RichTextState().apply { setHtml(originalHtml) }

            // First half keeps [0, cursor), removes [cursor, fullText.length)
            textBlock.state.removeTextRange(TextRange(cursor, fullText.length))

            // Second half keeps [cursor, fullText.length), removes [0, cursor)
            nextState.removeTextRange(TextRange(0, cursor))

            // Clean split boundary newlines if any
            if (nextState.annotatedString.text.startsWith('\n')) {
                nextState.removeTextRange(TextRange(0, 1))
            }
            val textLen = textBlock.state.annotatedString.text.length
            if (textLen > 0 && textBlock.state.annotatedString.text.endsWith('\n')) {
                textBlock.state.removeTextRange(TextRange(textLen - 1, textLen))
            }

            blocks.addAll(insertIndex, newBlocks)
            insertIndex += newBlocks.size

            nextTextBlock = EditorBlock.Text(nextState.apply { selection = TextRange.Zero })
            blocks.add(insertIndex, nextTextBlock)
        } else {
            val trimmedLen = fullText.trimEnd('\n').length
            if (trimmedLen < fullText.length) {
                textBlock.state.removeTextRange(TextRange(trimmedLen, fullText.length))
            }
            blocks.addAll(insertIndex, newBlocks)
            insertIndex += newBlocks.size

            nextTextBlock = EditorBlock.Text(RichTextState().apply { selection = TextRange.Zero })
            blocks.add(insertIndex, nextTextBlock)
        }

        activeBlockId = nextTextBlock.id
        focusTargetId = nextTextBlock.id
    }

    /**
     * Helper to concatenate two HTML fragments.
     * Only strips closing </p> and opening <p> when BOTH fragments are actual paragraphs.
     * Otherwise falls back to clean concatenation (preserving lists and other tags).
     */
    private fun mergeHtmlContent(firstHtml: String, secondHtml: String): String {
        val endsWithP = Regex("</p>\\s*$", RegexOption.IGNORE_CASE).containsMatchIn(firstHtml)
        val startsWithP = Regex("^\\s*<p[^>]*>", RegexOption.IGNORE_CASE).containsMatchIn(secondHtml)

        return if (endsWithP && startsWithP) {
            val cleanFirst = firstHtml.replace(Regex("</p>\\s*$", RegexOption.IGNORE_CASE), "")
            val cleanSecond = secondHtml.replace(Regex("^\\s*<p[^>]*>", RegexOption.IGNORE_CASE), "")
            cleanFirst + cleanSecond
        } else {
            firstHtml + secondHtml
        }
    }

    /**
     * Merges any adjacent [EditorBlock.Text] blocks into a single block.
     * Preserves cursor position if the currently focused block is merged away.
     */
    fun mergeAdjacentTextBlocks() {
        var i = 0
        while (i < blocks.size - 1) {
            val first = blocks[i]
            val second = blocks[i + 1]
            if (first is EditorBlock.Text && second is EditorBlock.Text) {
                val firstLength = first.state.annotatedString.text.length
                val firstHtml = first.state.toHtml()
                val secondHtml = second.state.toHtml()
                val isSecondActive = activeBlockId == second.id
                val secondCursor = if (isSecondActive) {
                    second.state.selection.max.coerceAtLeast(0)
                } else {
                    0
                }

                val mergedHtml = mergeHtmlContent(firstHtml, secondHtml)
                first.state.setHtml(mergedHtml)

                if (isSecondActive) {
                    first.state.selection = TextRange(firstLength + secondCursor)
                    activeBlockId = first.id
                    focusTargetId = first.id
                }

                blocks.removeAt(i + 1)
            } else {
                i++
            }
        }
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
                mergeAdjacentTextBlocks()
                if (blocks.none { it.id == activeBlockId }) {
                    activeBlockId = currentBlock.id
                    focusTargetId = currentBlock.id
                }
            }
            is EditorBlock.Image -> {
                removeBlock(prevBlock)
                if (blocks.none { it.id == activeBlockId }) {
                    activeBlockId = currentBlock.id
                    focusTargetId = currentBlock.id
                }
            }
            is EditorBlock.Text -> {
                val currentText = currentBlock.state.annotatedString.text
                if (currentText.isEmpty()) {
                    // Current block is blank: simply remove it and focus end of previous block
                    val prevLength = prevBlock.state.annotatedString.text.length
                    blocks.removeAt(currentIndex)
                    prevBlock.state.selection = TextRange(prevLength)
                    activeBlockId = prevBlock.id
                    focusTargetId = prevBlock.id
                } else {
                    // Merge current block into previous block
                    val prevLength = prevBlock.state.annotatedString.text.length
                    val prevHtml = prevBlock.state.toHtml()
                    val currentHtml = currentBlock.state.toHtml()
                    blocks.removeAt(currentIndex)

                    val mergedHtml = mergeHtmlContent(prevHtml, currentHtml)
                    prevBlock.state.setHtml(mergedHtml)
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
        } else {
            mergeAdjacentTextBlocks()
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
                    val plain = block.state.annotatedString.text.trim()
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

            // Delete any originally loaded images that were removed during this editing session
            val remainingImageUris = allImages.toSet()
            val removedOriginalImages = initialLoadedImages.filter { it !in remainingImageUris }
            removedOriginalImages.forEach { uriStr ->
                deleteInternalMediaFile(uriStr)
            }
            initialLoadedImages.clear()

            // Committed: clear session set so newly added files are preserved on disk
            sessionCopiedImages.clear()
            reset()
            onSave?.invoke(savedTitle, contentHtml, savedImages)
        }
    }

    fun discard() {
        initialLoadedImages.clear()
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

    fun loadNote(note: com.personalproject.mydiary.domain.timeline.model.NoteEntry) {
        loadContent(
            newTitle = note.title,
            rawContent = note.body.ifEmpty { note.bodyPlain },
            imageUris = note.imageUris
        )
    }

    /**
     * Populates the editor with specific title, raw content (HTML or plain text), and media URIs.
     */
    fun loadContent(newTitle: String, rawContent: String, imageUris: List<String>) {
        cleanupSessionImages()
        initialLoadedImages.clear()
        initialLoadedImages.addAll(imageUris)
        blocks.clear()
        fallbackRichTextState.clear()
        title = newTitle

        val parsedBlocks = parseDiaryDocument(rawContent, imageUris)
        parsedBlocks.filterIsInstance<EditorBlock.Image>().forEach {
            initialLoadedImages.add(it.uriString)
        }

        if (parsedBlocks.isEmpty()) {
            val initialBlock = EditorBlock.Text(fallbackRichTextState)
            blocks.add(initialBlock)
        } else {
            blocks.addAll(parsedBlocks)
        }

        activeBlockId = blocks.firstOrNull()?.id
        focusTargetId = blocks.filterIsInstance<EditorBlock.Text>().firstOrNull()?.id
        showFormattingBar = true
        selectedRange = null
    }

    fun reset() {
        title = ""
        initialLoadedImages.clear()
        cleanupSessionImages()
        blocks.clear()
        fallbackRichTextState.clear()
        val initialBlock = EditorBlock.Text(fallbackRichTextState)
        blocks.add(initialBlock)
        activeBlockId = initialBlock.id
        focusTargetId = null
        showFormattingBar = true
        selectedRange = null
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
