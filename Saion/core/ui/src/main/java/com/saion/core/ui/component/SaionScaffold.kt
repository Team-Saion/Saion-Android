package com.saion.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saion.ds.theme.SaionTheme

@Immutable
data class SystemBarInset(
    val statusBarInset: Boolean = true,
    val navigationBarInset: Boolean = true,
) {
    companion object {
        val Default: SystemBarInset = SystemBarInset(
            statusBarInset = true,
            navigationBarInset = true,
        )

        val None: SystemBarInset = SystemBarInset(
            statusBarInset = false,
            navigationBarInset = false,
        )
    }
}

@Composable
fun SaionScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = SaionTheme.colors.background.default,
    contentColor: Color = SaionTheme.colors.label.default,
    contentWindowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    systemBarInset: SystemBarInset = SystemBarInset.Default,
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        contentWindowInsets = contentWindowInsets,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .consumeWindowInsets(paddingValues = innerPadding)
                .applySystemBarInsetPadding(systemBarInset = systemBarInset),
            content = content,
        )
    }
}

private fun Modifier.applySystemBarInsetPadding(systemBarInset: SystemBarInset): Modifier {
    var modifier = this

    if (systemBarInset.statusBarInset) {
        modifier = modifier.statusBarsPadding()
    }

    if (systemBarInset.navigationBarInset) {
        modifier = modifier.navigationBarsPadding()
    }

    return modifier
}
