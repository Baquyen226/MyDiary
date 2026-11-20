package com.personalproject.mydiary.ui.feature.folder_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.profile.model.Profile
import com.personalproject.mydiary.ui.feature.common_component.editor.rememberAsyncImageBitmap
import androidx.core.graphics.toColorInt

@Composable
fun ProfileHeader(
    profile: Profile?,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val defaultHeaderBg = if (isDark) Color(0xFF2C394B) else Color(0xFF4A5D74)
    val headerBg = remember(profile?.themeColor, profile?.banner, isDark) {
        profile?.themeColor?.let { color ->
            if (color.startsWith("#")) {
                runCatching { Color(color.toColorInt()) }.getOrNull()
            } else null
        } ?: profile?.banner?.let { bg ->
            if (bg.startsWith("#")) {
                runCatching { Color(bg.toColorInt()) }.getOrNull()
            } else null
        } ?: defaultHeaderBg
    }
    val avatarBg = if (isDark) Color(0xFF3B4D63) else Color.White
    val bannerBitmap = rememberAsyncImageBitmap(profile?.banner.orEmpty())
    val avatarBitmap = rememberAsyncImageBitmap(profile?.avatar.orEmpty())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBg)
    ) {
        if (bannerBitmap != null) {
            Image(
                bitmap = bannerBitmap,
                contentDescription = stringResource(R.string.ui_profile_header_banner),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            // some gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                headerBg.copy(alpha = 0.40f),
                                headerBg.copy(alpha = 0.15f),
                                headerBg.copy(alpha = 0.60f)
                            )
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap,
                    contentDescription = stringResource(R.string.ui_profile_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(avatarBg)
                        .clickable(onClick = onAvatarClick)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.ui_profile_picture),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(avatarBg)
                        .clickable(onClick = onAvatarClick)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = profile?.name ?: stringResource(R.string.ui_profile_unknown_user),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                profile?.location?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
