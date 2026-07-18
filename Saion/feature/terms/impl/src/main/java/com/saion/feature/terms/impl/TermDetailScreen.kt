package com.saion.feature.terms.impl

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.saion.core.ui.component.SaionScaffold
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TermDetailScreen(
    url: String,
    onBack: () -> Unit,
) {
    var isLoading by remember(url) { mutableStateOf(true) }
    var webView: WebView? by remember { mutableStateOf(null) }

    BackHandler(onBack = onBack)

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }

    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.terms_title_read_only),
                    onBack = onBack,
                ),
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?,
                        ): Boolean = false

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?,
                        ) {
                            isLoading = true
                        }

                        override fun onPageFinished(
                            view: WebView?,
                            url: String?,
                        ) {
                            isLoading = false
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { view ->
                if (view.url != url) {
                    isLoading = true
                    view.loadUrl(url)
                }
            },
        )

        if (isLoading) {
            SaionSpinner()
        }
    }
}
