package com.personalproject.mydiary.ui.feature.common_component.editor

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.R

/**
 * Displays an image full-width directly in the diary entry like a Word document or website article.
 */
@Composable
fun ArticleImageItem(
    uriString: String,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        runCatching {
            val uri = Uri.parse(uriString)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                android.graphics.ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }.getOrNull()
    }

    bitmap?.let {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Diary entry image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            if (onRemove != null) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ui_close),
                        contentDescription = "Remove image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
