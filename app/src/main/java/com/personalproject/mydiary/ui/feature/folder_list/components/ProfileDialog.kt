package com.personalproject.mydiary.ui.feature.folder_list.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.profile.model.Profile
import com.personalproject.mydiary.ui.feature.common_component.editor.rememberAsyncImageBitmap
import com.personalproject.mydiary.ui.theme.rememberTimelineCardPreference

@Composable
fun ProfileDialog(
    profile: Profile?,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val cardTheme = rememberTimelineCardPreference(profile?.themeColor)
    val accentColor = cardTheme.primaryTextColor
    val avatarBg = if (isDark) Color(0xFF3B4D63) else Color(0xFFE2E8F0)
    val defaultBannerBg = if (isDark) Color(0xFF2C394B) else Color(0xFF4A5D74)

    val themeColor = remember(profile?.themeColor, profile?.banner, isDark) {
        profile?.themeColor?.let { color ->
            if (color.startsWith("#")) {
                runCatching { Color(color.toColorInt()) }.getOrNull()
            } else null
        } ?: profile?.banner?.let { bg ->
            if (bg.startsWith("#")) {
                runCatching { Color(bg.toColorInt()) }.getOrNull()
            } else null
        } ?: defaultBannerBg
    }

    val bannerBitmap = rememberAsyncImageBitmap(profile?.banner.orEmpty())
    val avatarBitmap = rememberAsyncImageBitmap(profile?.avatar.orEmpty())
    val sharePlaceholderText = stringResource(R.string.ui_profile_share_placeholder)

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner & Overlapping Avatar section (no edit image button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    // Banner Image / Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(themeColor)
                    ) {
                        if (bannerBitmap != null) {
                            Image(
                                bitmap = bannerBitmap,
                                contentDescription = stringResource(R.string.ui_profile_banner_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gradient overlay using theme color to avoid clashing colors and match theme
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                themeColor.copy(alpha = 0.40f),
                                                themeColor.copy(alpha = 0.15f),
                                                themeColor.copy(alpha = 0.60f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Top header: more_vertical menu button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                IconButton(
                                    onClick = { menuExpanded = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = stringResource(R.string.ui_profile_more_options),
                                        tint = Color.White
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.ui_profile_edit)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                tint = accentColor
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            onEditClick()
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.ui_profile_share)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null,
                                                tint = accentColor
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            if (onShareClick != null) {
                                                onShareClick()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    sharePlaceholderText,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Avatar overlapping the bottom edge of the banner (no camera edit button)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(100.dp)
                    ) {
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap,
                                contentDescription = stringResource(R.string.ui_profile_picture),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(avatarBg)
                                    .border(3.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = stringResource(R.string.ui_profile_picture),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(avatarBg)
                                    .border(3.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name
                Text(
                    text = profile?.name ?: stringResource(R.string.ui_profile_unknown_user),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Details: Location, Phone Number, Note
                    ProfileInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = stringResource(R.string.ui_profile_location),
                        value = profile?.location?.takeIf { it.isNotBlank() } ?: stringResource(R.string.ui_profile_not_specified),
                        iconTint = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = stringResource(R.string.ui_profile_phone),
                        value = profile?.phoneNumber?.takeIf { it.isNotBlank() } ?: stringResource(R.string.ui_profile_not_specified),
                        iconTint = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileInfoRow(
                        icon = Icons.AutoMirrored.Filled.Notes,
                        label = stringResource(R.string.ui_profile_note),
                        value = profile?.note?.takeIf { it.isNotBlank() } ?: stringResource(R.string.ui_profile_no_notes),
                        iconTint = accentColor
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bottom actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = accentColor
                            )
                        ) {
                            Text(stringResource(R.string.ui_close))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}
