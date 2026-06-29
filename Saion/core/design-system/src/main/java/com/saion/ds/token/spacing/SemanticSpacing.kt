package com.saion.ds.token.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class SemanticSpacing(
    val v2: Dp,
    val v4: Dp,
    val v6: Dp,
    val v8: Dp,
    val v10: Dp,
    val v12: Dp,
    val v14: Dp,
    val v16: Dp,
    val v18: Dp,
    val v20: Dp,
    val v24: Dp,
    val v32: Dp,
    val v40: Dp,
    val v48: Dp,
    val v56: Dp,
    val v64: Dp,
)

internal fun createSemanticSpacingDefault(): SemanticSpacing = SemanticSpacing(
    v2 = PrimitiveSpacing.V2,
    v4 = PrimitiveSpacing.V4,
    v6 = PrimitiveSpacing.V6,
    v8 = PrimitiveSpacing.V8,
    v10 = PrimitiveSpacing.V10,
    v12 = PrimitiveSpacing.V12,
    v14 = PrimitiveSpacing.V14,
    v16 = PrimitiveSpacing.V16,
    v18 = PrimitiveSpacing.V18,
    v20 = PrimitiveSpacing.V20,
    v24 = PrimitiveSpacing.V24,
    v32 = PrimitiveSpacing.V32,
    v40 = PrimitiveSpacing.V40,
    v48 = PrimitiveSpacing.V48,
    v56 = PrimitiveSpacing.V56,
    v64 = PrimitiveSpacing.V64,
)
