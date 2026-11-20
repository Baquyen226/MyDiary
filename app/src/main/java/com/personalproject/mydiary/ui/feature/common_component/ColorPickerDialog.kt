package com.personalproject.mydiary.ui.feature.common_component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.personalproject.mydiary.R
import androidx.core.graphics.toColorInt

fun parseHexColor(hex: String?): Color? {
    if (hex.isNullOrBlank()) return null
    val trimmed = hex.trim()
    val formatted = if (!trimmed.startsWith("#")) "#$trimmed" else trimmed
    return try {
        if (formatted.length == 7 || formatted.length == 9) {
            Color(formatted.toColorInt())
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

fun formatHexColor(hex: String): String {
    val trimmed = hex.trim()
    val formatted = if (!trimmed.startsWith("#")) "#$trimmed" else trimmed
    return formatted.uppercase()
}

val PRESET_COLORS = listOf(
    "#E57373", // Red
    "#F06292", // Pink
    "#BA68C8", // Purple
    "#7986CB", // Indigo
    "#1E81B0", // Classic Blue
    "#2596BE", // Cyan
    "#4DB6AC", // Teal
    "#81C784", // Green
    "#DCE775", // Lime
    "#FFD54F", // Amber
    "#FFB74D", // Orange
    "#A1887F"  // Brown
)

@Composable
fun ColorPickerContent(
    title: String = stringResource(R.string.ui_color_picker_change_color),
    initialColorHex: String?,
    fallbackColor: Color = Color(0xFF1E81B0),
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialDisplayHex = initialColorHex ?: "#1E81B0"
    var hexInput by remember(initialColorHex) { mutableStateOf(initialDisplayHex) }
    val currentColor = remember(initialColorHex) {
        parseHexColor(initialColorHex) ?: fallbackColor
    }
    val previewColor = remember(hexInput) {
        parseHexColor(hexInput)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comparison & Preview Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Current Color Display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.ui_color_picker_current), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = initialDisplayHex,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_ui_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Preview Color Display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.ui_color_picker_preview), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(previewColor ?: currentColor)
                        .border(
                            width = 2.dp,
                            color = if (previewColor != null) previewColor else MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (previewColor != null) formatHexColor(hexInput) else stringResource(R.string.ui_color_picker_invalid),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (previewColor != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hex Input Field
        OutlinedTextField(
            value = hexInput,
            onValueChange = {
                val filtered = it.filter { char -> char == '#' || char.isLetterOrDigit() }
                hexInput = filtered.take(9)
            },
            label = { Text(stringResource(R.string.ui_color_picker_hex_label)) },
            placeholder = { Text("#E57373") },
            singleLine = true,
            isError = previewColor == null && hexInput.isNotBlank(),
            supportingText = {
                if (previewColor == null && hexInput.isNotBlank()) {
                    Text(stringResource(R.string.ui_color_picker_hex_error))
                }
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(previewColor ?: Color.Transparent)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Palette
        Text(
            text = stringResource(R.string.ui_color_picker_presets),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PRESET_COLORS.forEach { hex ->
                val presetColor = parseHexColor(hex) ?: Color.Gray
                val isSelected = formatHexColor(hexInput) == hex
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(presetColor)
                        .clickable { hexInput = hex }
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(R.string.ui_dialog_cancel))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    if (previewColor != null) {
                        onColorSelected(formatHexColor(hexInput))
                    }
                },
                enabled = previewColor != null,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = previewColor ?: fallbackColor
                )
            ) {
                Text(stringResource(R.string.ui_color_picker_apply), color = Color.White)
            }
        }
    }
}

@Composable
fun DiaryColorPickerDialog(
    initialColorHex: String?,
    fallbackColor: Color = Color(0xFF1E81B0),
    title: String = stringResource(R.string.ui_color_picker_change_diary_color),
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f),
            shadowElevation = 8.dp
        ) {
            ColorPickerContent(
                title = title,
                initialColorHex = initialColorHex,
                fallbackColor = fallbackColor,
                onDismiss = onDismiss,
                onColorSelected = onColorSelected
            )
        }
    }
}
