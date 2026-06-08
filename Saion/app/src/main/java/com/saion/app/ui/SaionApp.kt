package com.saion.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.snackbar.LocalGlobalSnackbarState
import com.saion.ds.theme.SaionTheme

@Composable
fun SaionApp(modifier: Modifier = Modifier) {
    val snackbarState = remember { SnackbarHostState() }

    SaionTheme {
        CompositionLocalProvider(LocalGlobalSnackbarState provides snackbarState) {
            SaionScaffold(
                modifier = modifier,
                systemBarInset = SystemBarInset.None,
                snackbarHost = { SnackbarHost(hostState = LocalGlobalSnackbarState.current) },
            ) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SaionAppPreview() {
    SaionApp()
}
