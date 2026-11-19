package com.personalproject.mydiary.ui.feature.common_component.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import com.personalproject.mydiary.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.mohamedrejeb.richeditor.ui.BasicRichTextEditor
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import kotlinx.coroutines.launch
import java.io.File

/**
 * Common, self-contained diary editor component backing a Notion-like multi-block document.
 * Supports inline text, images, dividers, cross-block Backspace, escalating multi-block
 * "Select All" via LocalTextToolbar, and native camera captures.
 *
 * @param theme Color styling preferences for the editor.
 * @param modifier Modifier for the editor layout.
 * @param editorState The state holder for document blocks, title, formatting, and selection.
 * @param onSave Optional callback when entry is saved.
 * @param onDiscard Optional callback when entry is discarded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditor(
    theme: TimelineCardPreference,
    modifier: Modifier = Modifier,
    editorState: DiaryEditorState = rememberDiaryEditorState(),
    onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    onDiscard: (() -> Unit)? = null
) {
    if (onSave != null) editorState.onSave = onSave
    if (onDiscard != null) editorState.onDiscard = onDiscard

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // 1. Photo Picker Launcher (copies picked photos to internal storage asynchronously)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val persistentUris = uris.mapNotNull { uri ->
                    copyUriToInternalStorage(context, uri)
                }
                if (persistentUris.isNotEmpty()) {
                    editorState.insertImagesAfterCursor(persistentUris)
                }
            }
        }
    }

    // 2. Real Camera Launcher (captures directly into app internal storage via FileProvider)
    var cameraPhotoFile by remember { mutableStateOf<File?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = cameraPhotoFile
        if (success && file != null && file.exists() && file.length() > 0L) {
            editorState.insertImagesAfterCursor(listOf(file.toUri().toString()))
        } else if (file != null && file.exists()) {
            file.delete()
        }
        cameraPhotoFile = null
    }

    // Automatically focus the targeted text block (e.g. after inserting an image/divider or clicking empty space)
    LaunchedEffect(editorState.focusTargetId) {
        val targetId = editorState.focusTargetId
        if (targetId != null) {
            val targetBlock = editorState.blocks.filterIsInstance<EditorBlock.Text>().find { it.id == targetId }
            targetBlock?.focusRequester?.requestFocus()
            keyboardController?.show()
            editorState.focusTargetId = null
        }
    }

    ProvideEditorTextToolbar(editorState = editorState) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .onPreviewKeyEvent { event ->
                    // Hardware Ctrl+A: escalate to whole-document selection
                    if (event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.A) {
                        editorState.selectAllBlocks()
                        true
                    } else if (event.type == KeyEventType.KeyDown &&
                        (event.key == Key.Backspace || event.key == Key.Delete) &&
                        editorState.hasBlockSelection
                    ) {
                        editorState.deleteSelectedBlocks()
                        true
                    } else {
                        false
                    }
                }
        ) {
            // --- 1. Top Section (Title, Feature icons) ---
            EditorTopOptionsBar(
                title = editorState.title,
                onTitleChange = { editorState.title = it },
                showFormattingBar = editorState.showFormattingBar,
                onToggleFormattingBar = { editorState.showFormattingBar = !editorState.showFormattingBar },
                onInsertSeparator = {
                    editorState.insertDividerAfterCursor()
                },
                onPickImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onCameraClick = {
                    runCatching {
                        val mediaDir = File(context.filesDir, "diary_media").apply { if (!exists()) mkdirs() }
                        val photoFile = File(mediaDir, "camera_${System.currentTimeMillis()}.jpg")
                        val contentUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        cameraPhotoFile = photoFile
                        cameraLauncher.launch(contentUri)
                    }
                },
                onSelectCurrentBlock = { editorState.selectCurrentBlock() },
                onExtendSelectionUp = { editorState.extendSelectionUp() },
                onExtendSelectionDown = { editorState.extendSelectionDown() },
                onSelectAllBlocks = { editorState.selectAllBlocks() },
                hasSelection = editorState.hasBlockSelection,
                isAllBlocksSelected = editorState.isDocumentSelected,
                theme = theme
            )

            // --- 2. Main Document Body (Keyed Blocks + Empty space handler) ---
            val documentScrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val density = LocalDensity.current
                val thresholdPx = remember(density) { with(density) { 48.dp.toPx() } }
                val editorTextStyle = remember(theme.secondaryTextColor) {
                    TextStyle(
                        fontFamily = displayLargeFontFamily,
                        fontSize = 18.sp,
                        lineHeight = 32.sp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        color = theme.secondaryTextColor
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(documentScrollState)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    editorState.blocks.forEachIndexed { index, block ->
                        key(block.id) {
                            val isSelected = editorState.isBlockSelected(index)
                            var dragAccumulator by remember { mutableFloatStateOf(0f) }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .background(
                                                    theme.primaryTextColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .pointerInput(block.id, thresholdPx) {
                                                    detectDragGesturesAfterLongPress(
                                                        onDragStart = { dragAccumulator = 0f },
                                                        onDrag = { change, dragAmount ->
                                                            change.consume()
                                                            dragAccumulator = processBlockDrag(
                                                                dragY = dragAmount.y,
                                                                accumulator = dragAccumulator,
                                                                thresholdPx = thresholdPx,
                                                                onMoveUp = { editorState.moveSelectedBlocksUp() },
                                                                onMoveDown = { editorState.moveSelectedBlocksDown() }
                                                            )
                                                        },
                                                        onDragEnd = { dragAccumulator = 0f },
                                                        onDragCancel = { dragAccumulator = 0f }
                                                    )
                                                }
                                        } else Modifier
                                    )
                            ) {
                                when (block) {
                                    is EditorBlock.Text -> {
                                        BasicRichTextEditor(
                                            state = block.state,
                                            textStyle = editorTextStyle,
                                            cursorBrush = SolidColor(theme.primaryTextColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp)
                                                .padding(horizontal = 9.dp)
                                                .focusRequester(block.focusRequester)
                                                .onFocusChanged { focusState ->
                                                    if (focusState.isFocused) {
                                                        editorState.activeBlockId = block.id
                                                        if (editorState.hasBlockSelection) {
                                                            editorState.clearBlockSelection()
                                                        }
                                                    }
                                                }
                                                .onPreviewKeyEvent { event ->
                                                    // When Backspace is pressed at index 0, bridge to previous block
                                                    if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                                                        val sel = block.state.selection
                                                        if (sel.collapsed && sel.start == 0) {
                                                            editorState.handleBackspaceAtStart(block)
                                                            return@onPreviewKeyEvent true
                                                        }
                                                    }
                                                    false
                                                },
                                            decorationBox = { innerTextField ->
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    if (index == 0 && editorState.blocks.size == 1 && block.state.annotatedString.text.isEmpty()) {
                                                        Text(
                                                            text = stringResource(R.string.ui_folder_view_diary_body_prompt),
                                                            style = editorTextStyle.copy(
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                                textAlign = block.state.currentParagraphStyle.textAlign
                                                            ),
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )
                                    }

                                    is EditorBlock.Image -> {
                                        ArticleImageItem(
                                            uriString = block.uriString,
                                            isSelected = isSelected,
                                            onSelect = {
                                                if (isSelected) {
                                                    editorState.clearBlockSelection()
                                                } else {
                                                    editorState.selectBlock(index)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp)
                                                .padding(horizontal = 9.dp)
                                        )
                                    }

                                    is EditorBlock.Divider -> {
                                        EditorDividerItem(
                                            theme = theme,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .padding(horizontal = 9.dp)
                                        )
                                    }
                                }

                                // Dedicated visual drag handle when block is selected
                                if (isSelected && index == editorState.selectedRange?.first) {
                                    var handleDragAccumulator by remember { mutableFloatStateOf(0f) }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                                        shadowElevation = 3.dp,
                                        modifier = Modifier
                                            .align(if (block is EditorBlock.Image) Alignment.TopStart else Alignment.TopEnd)
                                            .padding(
                                                top = 2.dp,
                                                start = if (block is EditorBlock.Image) 4.dp else 0.dp,
                                                end = if (block is EditorBlock.Image) 0.dp else 4.dp
                                            )
                                            .pointerInput(block.id, thresholdPx) {
                                                detectDragGestures(
                                                    onDragStart = { handleDragAccumulator = 0f },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        handleDragAccumulator = processBlockDrag(
                                                            dragY = dragAmount.y,
                                                            accumulator = handleDragAccumulator,
                                                            thresholdPx = thresholdPx,
                                                            onMoveUp = { editorState.moveSelectedBlocksUp() },
                                                            onMoveDown = { editorState.moveSelectedBlocksDown() }
                                                        )
                                                    },
                                                    onDragEnd = { handleDragAccumulator = 0f },
                                                    onDragCancel = { handleDragAccumulator = 0f }
                                                )
                                            }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_editor_select_block_move),
                                                contentDescription = "Drag to move block",
                                                modifier = Modifier.size(18.dp),
                                                tint = theme.primaryTextColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tapping blank canvas space below the text cleanly moves the cursor to the end
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                editorState.onBlankAreaClicked()
                            }
                    )
                }

                // Floating Action Pill when block selection is active
                if (editorState.hasBlockSelection) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val count = editorState.selectedRange?.count() ?: 0
                            val labelText = if (count >= editorState.blocks.size) {
                                stringResource(R.string.ui_editor_all_blocks_selected)
                            } else {
                                stringResource(R.string.ui_editor_blocks_selected, count)
                            }
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            val canMoveUp = (editorState.selectedRange?.first ?: 0) > 0
                            val canMoveDown = (editorState.selectedRange?.last ?: (editorState.blocks.size - 1)) < editorState.blocks.size - 1

                            IconButton(
                                onClick = { editorState.moveSelectedBlocksUp() },
                                enabled = canMoveUp,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = stringResource(R.string.ui_editor_move_up),
                                    modifier = Modifier.size(20.dp),
                                    tint = if (canMoveUp) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                            IconButton(
                                onClick = { editorState.moveSelectedBlocksDown() },
                                enabled = canMoveDown,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = stringResource(R.string.ui_editor_move_down),
                                    modifier = Modifier.size(20.dp),
                                    tint = if (canMoveDown) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                            TextButton(
                                onClick = { editorState.copySelectedToClipboard(context) }
                            ) {
                                Text(stringResource(R.string.ui_editor_copy), color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(
                                onClick = { editorState.deleteSelectedBlocks() }
                            ) {
                                Text(stringResource(R.string.ui_editor_delete), color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // --- 3. Bottom Formatting Bar (LazyRow) ---
            EditorFormattingBar(
                state = editorState.activeRichTextState,
                theme = theme,
                visible = editorState.showFormattingBar,
                onToggleSpanStyle = { editorState.toggleSpanStyle(it) },
                onAddParagraphStyle = { editorState.addParagraphStyle(it) },
                onToggleUnorderedList = { editorState.toggleUnorderedList() },
                onToggleOrderedList = { editorState.toggleOrderedList() },
                onClose = { editorState.showFormattingBar = false }
            )
        }
    }
}

/**
 * Clean native divider block offering an explicit removal control for touch interfaces.
 */
@Composable
fun EditorDividerItem(
    modifier: Modifier = Modifier,
    theme: TimelineCardPreference,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.5.dp,
            color = theme.primaryTextColor.copy(alpha = 0.4f)
        )
    }
}

/**
 * Wraps LocalTextToolbar to intercept native "Select All" in Compose.
 * When the active block is already fully selected, clicking "Select All" escalates
 * to select all blocks across the entire document.
 */
@Composable
private fun ProvideEditorTextToolbar(
    editorState: DiaryEditorState,
    content: @Composable () -> Unit
) {
    val defaultToolbar = LocalTextToolbar.current
    val context = LocalContext.current

    val customToolbar = remember(defaultToolbar, editorState) {
        object : TextToolbar {
            override val status: TextToolbarStatus get() = defaultToolbar.status

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                val wrappedSelectAll: (() -> Unit)? = if (onSelectAllRequested != null) {
                    {
                        if (editorState.isCurrentBlockFullySelected()) {
                            editorState.selectAllBlocks()
                        } else {
                            onSelectAllRequested()
                        }
                    }
                } else {
                    // Fallback when native onSelectAllRequested is null (e.g. OEM toolbars or full single-block selection)
                    {
                        editorState.selectAllBlocks()
                    }
                }

                val wrappedCopy: () -> Unit = {
                    if (editorState.hasBlockSelection) {
                        editorState.copySelectedToClipboard(context)
                    } else {
                        onCopyRequested?.invoke()
                    }
                }

                val wrappedCut: () -> Unit = {
                    if (editorState.hasBlockSelection) {
                        editorState.cutAllToClipboard(context)
                    } else {
                        onCutRequested?.invoke()
                    }
                }

                defaultToolbar.showMenu(
                    rect,
                    wrappedCopy,
                    onPasteRequested,
                    wrappedCut,
                    wrappedSelectAll
                )
            }

            override fun hide() = defaultToolbar.hide()
        }
    }

    CompositionLocalProvider(LocalTextToolbar provides customToolbar) {
        content()
    }
}

private fun processBlockDrag(
    dragY: Float,
    accumulator: Float,
    thresholdPx: Float,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
): Float {
    val total = accumulator + dragY
    return when {
        total < -thresholdPx -> {
            onMoveUp()
            0f
        }
        total > thresholdPx -> {
            onMoveDown()
            0f
        }
        else -> total
    }
}
