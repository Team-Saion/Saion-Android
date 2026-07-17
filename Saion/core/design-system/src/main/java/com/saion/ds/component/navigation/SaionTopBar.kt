package com.saion.ds.component.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme

@Immutable
sealed interface TopBarVariant {
    data class Large(val title: String) : TopBarVariant
    data class Standard(
        val title: String? = null,
        val onBack: () -> Unit,
        val navigationIcon: ImageVector = SaionIcons.ChevronLeft,
    ) : TopBarVariant

    data object Main : TopBarVariant
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaionTopBar(
    variant: TopBarVariant,
    modifier: Modifier = Modifier,
    containerColor: Color = if (variant == TopBarVariant.Main) Color.Transparent else SaionTheme.colors.background.default,
    titleContentColor: Color = SaionTheme.colors.label.default,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = containerColor,
        titleContentColor = titleContentColor,
    )
    when (variant) {
        is TopBarVariant.Large -> TopAppBar(
            title = { TopBarTitle(title = variant.title, textStyle = SaionTheme.typography.heading1) },
            actions = actions,
            modifier = modifier.height(64.dp),
            colors = colors,
        )

        is TopBarVariant.Standard -> CenterAlignedTopAppBar(
            title = { TopBarTitle(title = variant.title.orEmpty(), textStyle = SaionTheme.typography.title2) },
            actions = actions,
            modifier = modifier.height(52.dp),
            colors = colors,
            navigationIcon = {
                SaionIconButton(
                    icon = variant.navigationIcon,
                    size = IconButtonSize.LARGE,
                    onClick = variant.onBack,
                )
            },
        )

        TopBarVariant.Main -> TopAppBar(
            title = {
                Icon(
                    imageVector = SaionIcons.Logo,
                    contentDescription = "",
                    modifier = Modifier.height(20.dp),
                )
            },
            actions = actions,
            modifier = modifier.height(44.dp),
            colors = colors,
        )
    }
}

@Composable
private fun TopBarTitle(
    title: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = textStyle,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun SaionTopBarLargePreview() {
    SaionTheme {
        SaionTopBar(
            variant = TopBarVariant.Large(title = "Title"),
            actions = {
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionTopBarStandardPreview() {
    SaionTheme {
        SaionTopBar(
            variant = TopBarVariant.Standard(title = "Title", onBack = {}),
            actions = {
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionTopBarMainPreview() {
    SaionTheme {
        SaionTopBar(
            variant = TopBarVariant.Main,
            actions = {
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
                SaionIconButton(icon = SaionIcons.Selection, onClick = {})
            },
        )
    }
}
