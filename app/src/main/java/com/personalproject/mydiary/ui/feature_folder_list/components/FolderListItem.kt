package com.personalproject.mydiary.ui.feature_folder_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.model.folder.model.FolderType
import com.personalproject.mydiary.ui.feature_folder_list.FolderSummary

import androidx.core.graphics.toColorInt

@Composable
fun FolderListItem(
    summary: FolderSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val folder = summary.folder
    val iconRes = when (folder.type) {
        FolderType.CONTACTS -> R.drawable.ic_folderview_type_contact
        FolderType.DIARY -> R.drawable.ic_folderview_type_diary
        FolderType.EVENT -> R.drawable.ic_folderview_type_event
        FolderType.NOTES -> R.drawable.ic_folderview_type_notes
    }
    
    val textColor = folder.color?.let { Color(it.toColorInt()) } ?: Color.DarkGray

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 24.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = textColor
            )
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Text(
                text = folder.name,
                modifier = Modifier.weight(1f),
                color = textColor,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = summary.itemCount.toString(),
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            Icon(
                painter = painterResource(id = R.drawable.ic_ui_arrow),
                contentDescription = null,
                modifier = Modifier.size(16.dp).rotate(180.0f),
                tint = textColor.copy(alpha = 0.5f)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
    }
}
