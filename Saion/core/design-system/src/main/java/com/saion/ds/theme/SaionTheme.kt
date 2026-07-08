@file:Suppress(
    "CompositionLocalAllowlist",
    "ktlint:compose:compositionlocal-allowlist",
)

package com.saion.ds.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.saion.ds.token.color.SemanticColor
import com.saion.ds.token.color.createSemanticColorLight
import com.saion.ds.token.elevation.SaionShadow
import com.saion.ds.token.elevation.createSaionShadow
import com.saion.ds.token.radius.SemanticRadius
import com.saion.ds.token.radius.createSemanticRadiusDefault
import com.saion.ds.token.spacing.SemanticSpacing
import com.saion.ds.token.spacing.createSemanticSpacingDefault
import com.saion.ds.token.typography.SemanticTypography
import com.saion.ds.token.typography.createSemanticTypographyDefault

internal val LocalSaionColors =
    staticCompositionLocalOf<SemanticColor> { error("No SemanticColor provided") }
internal val LocalSaionTypography =
    staticCompositionLocalOf<SemanticTypography> { error("No SemanticTypography provided") }
internal val LocalSaionRadius =
    staticCompositionLocalOf<SemanticRadius> { error("No SemanticRadius provided") }
internal val LocalSaionSpacing =
    staticCompositionLocalOf<SemanticSpacing> { error("No SemanticSpacing provided") }
internal val LocalSaionShadow =
    staticCompositionLocalOf<SaionShadow> { error("No SaionShadow provided") }

@Stable
object SaionTheme {
    val colors: SemanticColor
        @Composable
        @ReadOnlyComposable
        get() = LocalSaionColors.current

    val typography: SemanticTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalSaionTypography.current

    val radius: SemanticRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalSaionRadius.current

    val shadow: SaionShadow
        @Composable
        @ReadOnlyComposable
        get() = LocalSaionShadow.current

    val spacing: SemanticSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSaionSpacing.current
}

@Composable
fun SaionTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSaionColors provides createSemanticColorLight(),
        LocalSaionTypography provides createSemanticTypographyDefault(),
        LocalSaionRadius provides createSemanticRadiusDefault(),
        LocalSaionSpacing provides createSemanticSpacingDefault(),
        LocalSaionShadow provides createSaionShadow(),
    ) {
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = LocalSaionColors.current.primary.default,
                backgroundColor = LocalSaionColors.current.primary.default.copy(alpha = 0.3f),
            ),
            content = content,
        )
    }
}
