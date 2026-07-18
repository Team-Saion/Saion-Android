package com.saion.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.saion.ds.component.image.SaionImage
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

@Composable
fun SaionProfile(
    nickname: String,
    textStyle: TextStyle,
    imageUrl: String?,
    avatarColorHex: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    isShowEdit: Boolean = false,
) {
    val shape = SaionTheme.radius.component.full.toRoundedCornerShape()

    Box(modifier = modifier.fillMaxWidth()) {
        SaionImage(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .clip(shape),
            contentScale = ContentScale.Crop,
            fallback = {
                SaionProfileFallback(
                    nickname = nickname,
                    textStyle = textStyle,
                    avatarColorHex = avatarColorHex,
                )
            },
        )

        if (isShowEdit) {
            Icon(
                imageVector = SaionIcons.Settings,
                contentDescription = null,
                tint = SaionTheme.colors.background.muted,
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        shape = shape,
                        color = SaionTheme.colors.label.subtle,
                    )
                    .padding(4.dp)
                    .align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun SaionProfileFallback(
    nickname: String,
    textStyle: TextStyle,
    avatarColorHex: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFromHexOrDefault(avatarColorHex)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = nickname.trim().take(2),
            style = textStyle,
            color = SaionTheme.colors.label.inverse,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

private fun colorFromHexOrDefault(hex: String): Color = runCatching { Color(hex.toColorInt()) }.getOrElse { Color(0xFFE6E6E6) }

@Preview(showBackground = true)
@Composable
private fun SaionProfileFallbackPreview() {
    SaionTheme {
        SaionProfile(
            nickname = "사이온",
            textStyle = SaionTheme.typography.display2,
            imageUrl = null,
            avatarColorHex = "#FFD35C",
            contentDescription = null,
            modifier = Modifier.size(80.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionProfileImagePreview() {
    SaionTheme {
        SaionProfile(
            nickname = "사이온",
            textStyle = SaionTheme.typography.display2,
            imageUrl = "https://example.com/profile.png",
            avatarColorHex = "#FFD35C",
            contentDescription = null,
            modifier = Modifier.size(80.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionProfileImageShowEditPreview() {
    SaionTheme {
        SaionProfile(
            nickname = "사이온",
            textStyle = SaionTheme.typography.display2,
            imageUrl = "https://example.com/profile.png",
            avatarColorHex = "#FFD35C",
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            isShowEdit = true,
        )
    }
}
