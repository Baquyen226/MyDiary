package com.personalproject.mydiary.ui.feature.common_component.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * Common, self-contained diary editor component.
 * Displays content and images inline like Word or an article.
 * Supports cursor-relative image insertion and tapping blank lines to move cursor anywhere.
 *
 * @param theme Color styling preferences for the editor.
 * @param modifier Modifier for the editor layout.
 * @param editorState The state holder for title, rich text, formatting bar, images, and voice recording.
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

    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            editorState.insertImagesAfterCursor(uris.map { it.toString() })
        }
    }

    // Automatically focus the targeted text block (e.g. after inserting an image or clicking a blank line)
    LaunchedEffect(editorState.focusTargetId) {
        val targetId = editorState.focusTargetId
        if (targetId != null) {
            val targetBlock = editorState.blocks.filterIsInstance<EditorBlock.Text>().find { it.id == targetId }
            targetBlock?.focusRequester?.requestFocus()
            keyboardController?.show()
            editorState.focusTargetId = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // --- 1. Top Section (Title, Feature icons, Voice indicator) ---
        EditorTopOptionsBar(
            title = editorState.title,
            onTitleChange = { editorState.title = it },
            showFormattingBar = editorState.showFormattingBar,
            onToggleFormattingBar = { editorState.showFormattingBar = !editorState.showFormattingBar },
            onInsertSeparator = {
                editorState.activeRichTextState.addTextAfterSelection("\n────────────────────────────\n")
            },
            onPickImage = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onCameraClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            isRecordingVoice = editorState.isRecordingVoice,
            onToggleVoiceRecord = { editorState.isRecordingVoice = !editorState.isRecordingVoice },
            theme = theme
        )

        // --- 2. Main Document Body (Text & Images displayed in article layout like Word) ---
        val documentScrollState = rememberScrollState()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val viewportHeight = maxHeight
            var contentHeightPx by remember { mutableStateOf(0) }
            val viewportHeightPx = with(density) { viewportHeight.toPx() }
            val remainingHeightDp = with(density) {
                (viewportHeightPx - contentHeightPx).coerceAtLeast(0f).toDp()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(documentScrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            contentHeightPx = coordinates.size.height
                        }
                ) {
                    val isDocumentEmpty = editorState.isEmpty()

                    editorState.blocks.forEachIndexed { index, block ->
                        when (block) {
                            is EditorBlock.Text -> {
                                RichTextEditor(
                                    state = block.state,
                                    placeholder = {
                                        if (index == 0 && isDocumentEmpty) {
                                            Text(
                                                text = "What happened today?",
                                                fontFamily = displayLargeFontFamily,
                                                fontSize = 18.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    textStyle = TextStyle(
                                        fontFamily = displayLargeFontFamily,
                                        fontSize = 18.sp,
                                        lineHeight = 24.sp,
                                        color = theme.secondaryTextColor
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(block.focusRequester)
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                editorState.activeBlockId = block.id
                                            }
                                        },
                                    colors = RichTextEditorDefaults.richTextEditorColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    )
                                )
                            }
                            is EditorBlock.Image -> {
                                ArticleImageItem(
                                    uriString = block.uriString,
                                    onRemove = {
                                        editorState.removeBlock(block)
                                    }
                                )
                            }
                        }
                    }
                }

                // Blank space area allowing cursor to navigate to any blank line
                val blankAreaHeight = if (remainingHeightDp > 0.dp) remainingHeightDp else 48.dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(blankAreaHeight)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                editorState.moveToBlankLine(offset.y, density)
                            }
                        }
                )
            }
        }

        // --- 3. Bottom Formatting Bar (LazyRow) ---
        EditorFormattingBar(
            state = editorState.activeRichTextState,
            theme = theme,
            visible = editorState.showFormattingBar
        )
    }
}
