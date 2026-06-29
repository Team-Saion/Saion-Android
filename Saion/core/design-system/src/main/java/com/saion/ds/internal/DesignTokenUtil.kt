package com.saion.ds.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.saion.ds.token.typography.Pretendard

@Composable
internal fun createSaionTextStyle(
    fontSize: Dp,
    fontWeight: FontWeight,
    lineHeight: Dp,
    letterSpacing: Dp,
    fontFamily: FontFamily = Pretendard,
) = with(LocalDensity.current) {
    TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize.toSp(),
        fontWeight = fontWeight,
        lineHeight = lineHeight.toSp(),
        letterSpacing = letterSpacing.toSp(),
    )
}
