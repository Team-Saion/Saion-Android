package com.saion.ds.component.button

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme

@Composable
fun SaionBottomCTA(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    upper: @Composable (() -> Unit)? = null,
    lower: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        upper?.let { upperContent ->
            upperContent()
            Spacer(modifier = Modifier.height(20.dp))
        }
        content()
        lower?.let { lowerContent ->
            Spacer(modifier = Modifier.height(18.dp))
            lowerContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionBottomCTALowerPreview() {
    SaionTheme {
        SaionBottomCTA(
            lower = {
                SaionTextButton(
                    text = "닫기",
                    onClick = {},
                    size = TextButtonSize.MEDIUM,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        ) {
            SaionButtonArea(
                mainButton = { modifier ->
                    SaionButton(
                        text = "동의하고 다음",
                        onClick = {},
                        size = ButtonSize.XLARGE,
                        modifier = modifier,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionBottomCTAUpperPreview() {
    SaionTheme {
        SaionBottomCTA(
            upper = {
                Text(text = "10자 이내로 입력해주세요.")
            },
        ) {
            SaionButtonArea(
                mainButton = { modifier ->
                    SaionButton(
                        text = "시작하기",
                        onClick = {},
                        size = ButtonSize.XLARGE,
                        modifier = modifier,
                    )
                },
            )
        }
    }
}
