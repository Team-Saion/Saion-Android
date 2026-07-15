package com.saion.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.selection.ChipShape
import com.saion.ds.component.selection.ChipSize
import com.saion.ds.component.selection.SaionChip
import com.saion.ds.theme.SaionTheme

@Composable
internal fun HomeTitleSection(
    circleName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.linearGradient(
                        0f to SaionTheme.colors.background.default,
                        0.5f to SaionTheme.colors.line.default,
                        1f to SaionTheme.colors.background.default,
                    ),
                ),
        )
        SaionChip(
            text = circleName,
            size = ChipSize.SMALL,
            shape = ChipShape.PILL,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTitleSectionPreview() {
    SaionTheme {
        HomeTitleSection(
            circleName = "써클이름",
        )
    }
}
