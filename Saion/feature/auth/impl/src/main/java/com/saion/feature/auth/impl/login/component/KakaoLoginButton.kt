package com.saion.feature.auth.impl.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.auth.impl.R

@Composable
internal fun KakaoLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape = SaionTheme.radius.component.full.toRoundedCornerShape())
            .background(Color(0xFFFEE500))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = SaionIcons.KakaoLogo,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = stringResource(R.string.login_with_kakao),
            color = SaionTheme.colors.label.default,
            style = SaionTheme.typography.title1Subtle,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KakaoLoginButtonPreview() {
    SaionTheme {
        KakaoLoginButton(onClick = {}, modifier = Modifier.padding(8.dp))
    }
}
