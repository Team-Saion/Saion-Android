package com.saion.feature.auth.impl.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme

@Composable
internal fun LoginBottomContent(
    alpha: Float,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .graphicsLayer(alpha = alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KakaoLoginButton(
            onClick = onLoginClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginBottomContentPreview() {
    SaionTheme {
        LoginBottomContent(
            alpha = 1f,
            onLoginClick = {},
        )
    }
}
