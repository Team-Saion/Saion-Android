package com.saion.core.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.CollectWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    collector: suspend CoroutineScope.(T) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(this, lifecycle, minActiveState) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            collect { value -> collector(value) }
        }
    }
}
