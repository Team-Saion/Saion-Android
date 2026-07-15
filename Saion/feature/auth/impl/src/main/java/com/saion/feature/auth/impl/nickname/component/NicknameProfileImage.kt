package com.saion.feature.auth.impl.nickname.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.saion.ds.component.image.SaionImage
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.auth.impl.R

@Composable
internal fun NicknameProfileImage(
    imageUrl: String?,
    avatarColorHex: String,
    modifier: Modifier = Modifier,
) {
    SaionImage(
        imageUrl = imageUrl,
        contentDescription = stringResource(R.string.nickname_profile_image_description),
        modifier = modifier
            .size(80.dp)
            .clip(SaionTheme.radius.component.full.toRoundedCornerShape()),
        contentScale = ContentScale.Crop,
        fallback = {
            NicknameProfileFallbackImage(hexColor = avatarColorHex)
        },
    )
}

@Composable
private fun NicknameProfileFallbackImage(
    hexColor: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(SaionTheme.radius.component.full.toRoundedCornerShape())
            .background(colorFromHexOrDefault(hexColor)),
    )
}

private fun colorFromHexOrDefault(hex: String): Color = runCatching { Color(hex.toColorInt()) }.getOrElse { Color(0xFFFFD35C) }

@Preview(showBackground = true)
@Composable
private fun NicknameProfileImagePreview() {
    SaionTheme {
        NicknameProfileImage(
            imageUrl = null,
            avatarColorHex = "#FFD35C",
        )
    }
}
