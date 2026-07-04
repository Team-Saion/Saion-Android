package com.saion.ds.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.theme.SaionTheme

@Composable
fun SaionButtonArea(
    mainButton: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = true,
    subButton: @Composable ((Modifier) -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (isVertical) {
            VerticalButtonArea(mainButton = mainButton, subButton = subButton)
        } else {
            HorizontalButtonArea(mainButton = mainButton, subButton = subButton)
        }
    }
}

@Composable
private fun VerticalButtonArea(
    mainButton: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    subButton: @Composable ((Modifier) -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        mainButton(Modifier.fillMaxWidth())
        subButton?.let { button -> button(Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun HorizontalButtonArea(
    mainButton: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    subButton: @Composable ((Modifier) -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        mainButton(Modifier.weight(1f))
        subButton?.let { button -> button(Modifier.weight(1f)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionButtonAreaSinglePreview() {
    SaionTheme {
        SaionButtonArea(
            mainButton = { modifier ->
                SaionButton(
                    text = "Button",
                    onClick = {},
                    size = ButtonSize.XLARGE,
                    modifier = modifier,
                )
            },
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionButtonAreaVerticalPreview() {
    SaionTheme {
        SaionButtonArea(
            mainButton = { modifier ->
                SaionButton(
                    text = "Button",
                    onClick = {},
                    size = ButtonSize.XLARGE,
                    modifier = modifier,
                )
            },
            subButton = { modifier ->
                SaionButton(
                    text = "Button",
                    onClick = {},
                    size = ButtonSize.XLARGE,
                    variant = ButtonVariant.NEUTRAL,
                    modifier = modifier,
                )
            },
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionButtonAreaHorizontalPreview() {
    SaionTheme {
        SaionButtonArea(
            mainButton = { modifier ->
                SaionButton(
                    text = "Button",
                    onClick = {},
                    size = ButtonSize.XLARGE,
                    modifier = modifier,
                )
            },
            subButton = { modifier ->
                SaionButton(
                    text = "Button",
                    onClick = {},
                    size = ButtonSize.XLARGE,
                    variant = ButtonVariant.NEUTRAL,
                    modifier = modifier,
                )
            },
            isVertical = false,
            modifier = Modifier.padding(20.dp),
        )
    }
}
