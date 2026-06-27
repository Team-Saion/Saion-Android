package com.saion.core.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner

/**
 * 컴포넌트 단위로 독립적인 ViewModel 저장소와 saveable state 범위를 만듭니다.
 *
 * 같은 화면 안에서도 [key]가 다른 컴포넌트는 서로 다른 `ViewModelStoreOwner`를 사용하므로
 * `viewModel()`로 생성한 인스턴스와 `rememberSaveable` 상태를 분리해 유지할 수 있습니다.
 *
 * @param key 컴포넌트 범위를 식별하는 키입니다. 키가 바뀌면 해당 범위의 상태와 ViewModel도 새로 생성됩니다.
 * @see <a href="https://www.youtube.com/watch?v=XUvTOAtOPSM">참고 영상</a>
 */
@Composable
fun ComponentViewModelScope(
    key: Any,
    saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    content: @Composable () -> Unit,
) {
    saveableStateHolder.SaveableStateProvider(key) {
        val storeOwner = rememberViewModelStoreOwner()
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides storeOwner,
            content = content,
        )
    }
}
