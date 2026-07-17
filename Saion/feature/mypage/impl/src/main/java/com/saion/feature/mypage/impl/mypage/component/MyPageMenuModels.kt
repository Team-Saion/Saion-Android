package com.saion.feature.mypage.impl.mypage.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.saion.feature.mypage.impl.R

@Immutable
internal data class MyPageMenuItem(
    val title: String,
    val onClick: () -> Unit,
    val trailingContent: MyPageMenuTrailingContent = MyPageMenuTrailingContent.Chevron,
)

@Immutable
internal sealed interface MyPageMenuTrailingContent {
    @Immutable
    data object Chevron : MyPageMenuTrailingContent

    @Immutable
    data class TextButton(
        val text: String,
        val enabled: Boolean = true,
    ) : MyPageMenuTrailingContent
}

@Composable
internal fun versionMenuItem(
    versionName: String,
    isLatestVersion: Boolean,
    onUpdateClick: () -> Unit,
): MyPageMenuItem = MyPageMenuItem(
    title = stringResource(R.string.mypage_version_format, versionName),
    onClick = { if (isLatestVersion.not()) onUpdateClick() },
    trailingContent = MyPageMenuTrailingContent.TextButton(
        text = if (isLatestVersion) {
            stringResource(R.string.mypage_version_latest)
        } else {
            stringResource(R.string.mypage_version_update)
        },
    ),
)
