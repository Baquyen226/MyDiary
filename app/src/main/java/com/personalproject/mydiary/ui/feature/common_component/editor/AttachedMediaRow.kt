package com.personalproject.mydiary.ui.feature.common_component.editor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.personalproject.mydiary.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Copies a content URI to app internal storage so it remains persistently accessible across reboots.
 * Returns null if the copy fails, preventing 0-byte or corrupted entries.
 */
suspend fun copyUriToInternalStorage(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
    if (sourceUri.scheme == "file") {
        return@withContext sourceUri.toString()
    }
    runCatching {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@runCatching null
        val mediaDir = File(context.filesDir, "diary_media").apply { if (!exists()) mkdirs() }
        val targetFile = File(mediaDir, "img_${UUID.randomUUID()}.jpg")
        inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        if (targetFile.length() == 0L) {
            targetFile.delete()
            null
        } else {
            targetFile.toUri().toString()
        }
    }.getOrNull()
}

/**
 * Safely deletes a media file from internal storage if it was uncommitted or removed.
 */
fun deleteInternalMediaFile(uriString: String) {
    runCatching {
        val uri = uriString.toUri()
        val path = uri.path
        if (path != null) {
            val file = File(path)
            if (file.exists() && file.absolutePath.contains("diary_media")) {
                file.delete()
            }
        }
    }
}

/**
 * Loads, downsamples, and corrects EXIF orientation asynchronously off the main thread
 * with ARGB_8888 fidelity to prevent UI jank, OOM crashes, and color banding.
 */
@Composable
fun rememberAsyncImageBitmap(uriString: String, maxDimension: Int = 1080): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uriString, key2 = maxDimension) {
        // 1. Clear old bitmap immediately when URI changes to prevent stale image flash
        value = null
        if (uriString.isBlank()) return@produceState

        value = withContext(Dispatchers.IO) {
            runCatching {
                val uri = if (uriString.startsWith("/")) Uri.fromFile(java.io.File(uriString)) else uriString.toUri()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                        // Native exact scaling (ImageDecoder handles EXIF orientation automatically!)
                        val maxEdge = maxOf(info.size.width, info.size.height)
                        if (maxEdge > maxDimension) {
                            val scale = maxDimension.toFloat() / maxEdge
                            val targetWidth = (info.size.width * scale).toInt().coerceAtLeast(1)
                            val targetHeight = (info.size.height * scale).toInt().coerceAtLeast(1)
                            decoder.setTargetSize(targetWidth, targetHeight)
                        }
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                    bitmap.asImageBitmap()
                } else {
                    // 1. Measure dimensions without allocating pixel memory
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, options)
                    }

                    val (width, height) = options.outWidth to options.outHeight
                    if (width <= 0 || height <= 0) return@runCatching null

                    // 2. Compute proper power-of-two downsampling based on the longest edge
                    var inSampleSize = 1
                    val maxEdge = maxOf(width, height)
                    while (maxEdge / inSampleSize > maxDimension) {
                        inSampleSize *= 2
                    }

                    // 3. Decode scaled bitmap in ARGB_8888
                    val decodeOptions = BitmapFactory.Options().apply {
                        this.inSampleSize = inSampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream, null, decodeOptions)
                    } ?: return@runCatching null

                    // 4. Correct EXIF orientation
                    val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
                        ExifInterface(stream).getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                    } ?: ExifInterface.ORIENTATION_NORMAL

                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                    }

                    val finalBitmap = if (!matrix.isIdentity) {
                        val rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
                        decoded.recycle() // Free intermediate un-rotated buffer
                        rotated
                    } else {
                        decoded
                    }
                    finalBitmap.asImageBitmap()
                }
            }.getOrNull()
        }
    }.value
}

/**
 * Displays an image full-width directly in the diary entry with asynchronous loading,
 * downsampling, EXIF correction, and deliberate aspect ratio scaling.
 */
@Composable
fun ArticleImageItem(
    uriString: String,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bitmap = rememberAsyncImageBitmap(uriString = uriString)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Diary entry image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color.Gray
                )
            }
        }

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
