package com.saion.ds.token.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saion.ds.internal.createSaionTextStyle

@Immutable
data class SemanticTypography(
    val display1: TextStyle,
    val display2: TextStyle,
    val heading1: TextStyle,
    val heading2: TextStyle,
    val heading1Subtle: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val label1: TextStyle,
    val label2: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle,
    val title1Subtle: TextStyle,
    val title3Strong: TextStyle,
    val label1Strong: TextStyle,
    val label1Subtle: TextStyle,
    val label2Strong: TextStyle,
    val caption2Strong: TextStyle,
    val title1Strong: TextStyle,
)

@Composable
internal fun createSemanticTypographyDefault(): SemanticTypography = SemanticTypography(
    display1 = createSaionTextStyle(
        fontSize = 40.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 48.dp,
        letterSpacing = (-0.5).dp,
    ),
    display2 = createSaionTextStyle(
        fontSize = 32.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.dp,
        letterSpacing = (-0.5).dp,
    ),
    heading1 = createSaionTextStyle(
        fontSize = 24.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 31.dp,
        letterSpacing = (-0.5).dp,
    ),
    heading2 = createSaionTextStyle(
        fontSize = 20.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.dp,
        letterSpacing = 0.dp,
    ),
    heading1Subtle = createSaionTextStyle(
        fontSize = 22.dp,
        fontWeight = FontWeight.Medium,
        lineHeight = 31.dp,
        letterSpacing = 0.dp,
    ),
    title1 = createSaionTextStyle(
        fontSize = 18.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 25.dp,
        letterSpacing = 0.dp,
    ),
    title2 = createSaionTextStyle(
        fontSize = 16.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.dp,
        letterSpacing = 0.dp,
    ),
    title3 = createSaionTextStyle(
        fontSize = 15.dp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.dp,
        letterSpacing = 0.dp,
    ),
    body1 = createSaionTextStyle(
        fontSize = 16.dp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.dp,
        letterSpacing = 0.dp,
    ),
    body2 = createSaionTextStyle(
        fontSize = 14.dp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.dp,
        letterSpacing = 0.dp,
    ),
    body3 = createSaionTextStyle(
        fontSize = 13.dp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.dp,
        letterSpacing = 0.dp,
    ),
    label1 = createSaionTextStyle(
        fontSize = 14.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.dp,
        letterSpacing = 0.dp,
    ),
    label2 = createSaionTextStyle(
        fontSize = 12.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 17.dp,
        letterSpacing = 0.dp,
    ),
    caption1 = createSaionTextStyle(
        fontSize = 12.dp,
        fontWeight = FontWeight.Normal,
        lineHeight = 17.dp,
        letterSpacing = 0.dp,
    ),
    caption2 = createSaionTextStyle(
        fontSize = 11.dp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.dp,
        letterSpacing = 0.25.dp,
    ),
    title1Subtle = createSaionTextStyle(
        fontSize = 18.dp,
        fontWeight = FontWeight.Medium,
        lineHeight = 25.dp,
        letterSpacing = 0.dp,
    ),
    title3Strong = createSaionTextStyle(
        fontSize = 15.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.dp,
        letterSpacing = 0.dp,
    ),
    label1Strong = createSaionTextStyle(
        fontSize = 14.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.dp,
        letterSpacing = 0.dp,
    ),
    label1Subtle = createSaionTextStyle(
        fontSize = 14.dp,
        fontWeight = FontWeight.Medium,
        lineHeight = 21.dp,
        letterSpacing = 0.dp,
    ),
    label2Strong = createSaionTextStyle(
        fontSize = 12.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 17.dp,
        letterSpacing = 0.dp,
    ),
    caption2Strong = createSaionTextStyle(
        fontSize = 11.dp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 14.dp,
        letterSpacing = 0.25.dp,
    ),
    title1Strong = createSaionTextStyle(
        fontSize = 18.dp,
        fontWeight = FontWeight.Bold,
        lineHeight = 25.dp,
        letterSpacing = 0.dp,
    ),
)
