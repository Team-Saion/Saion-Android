package com.saion.core.ui.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.saion.ds.theme.SaionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaionPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorContainerColor: Color = SaionTheme.colors.background.default,
    indicatorColor: Color = SaionTheme.colors.primary.default,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                state = state,
                containerColor = indicatorContainerColor,
                color = indicatorColor,
            )
        },
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun SaionPullToRefreshBoxPreview() {
    SaionTheme {
        SaionPullToRefreshBox(
            isRefreshing = false,
            onRefresh = {},
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun SaionPullToRefreshBoxRefreshingPreview() {
    SaionTheme {
        SaionPullToRefreshBox(
            isRefreshing = true,
            onRefresh = {},
        ) {}
    }
}
