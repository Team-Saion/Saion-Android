package com.saion.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.ds.theme.SaionTheme

@Composable
fun SaionApp(modifier: Modifier = Modifier) {
    SaionTheme {
        LaunchedEffect(Unit) {
            GlobalUiEventBus.events.collect { event ->
                when (event) {
                    GlobalUiEvent.SessionExpired -> { /* todo: 세션 종료 스낵바, 첫화면으로 이동 처리 */ }
                }
            }
        }

        SaionScaffold(
            modifier = modifier,
            systemBarInset = SystemBarInset.None,
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SaionAppPreview() {
    SaionApp()
}
