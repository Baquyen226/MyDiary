package com.personalproject.mydiary.ui.feature.folder_view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.personalproject.mydiary.ui.feature.common_component.editor.DiaryEditor
import com.personalproject.mydiary.ui.feature.common_component.editor.DiaryEditorState
import com.personalproject.mydiary.ui.feature.common_component.editor.rememberDiaryEditorState
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

/**
 * Tab wrapper for the diary entry editor in FolderView.
 */
@Composable
fun EditorTab(
    theme: TimelineCardPreference,
    modifier: Modifier = Modifier,
    editorState: DiaryEditorState = rememberDiaryEditorState(),
    onSave: ((title: String, contentHtml: String, imageUris: List<String>) -> Unit)? = null,
    onDiscard: (() -> Unit)? = null
) {
    DiaryEditor(
        theme = theme,
        editorState = editorState,
        onSave = onSave,
        onDiscard = onDiscard,
        modifier = modifier
    )
}
