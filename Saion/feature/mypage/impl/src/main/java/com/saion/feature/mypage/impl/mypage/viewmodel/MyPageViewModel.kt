package com.saion.feature.mypage.impl.mypage.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.mypage.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class MyPageViewModel @Inject constructor(private val getMyInfoUseCase: GetMyInfoUseCase) :
    BaseViewModel<MyPageState, MyPageEffect, MyPageIntent>(MyPageState()) {
    init {
        loadMyInfo()
    }

    override fun handleIntent(intent: MyPageIntent) = Unit

    private fun loadMyInfo() {
        launchSafely(
            onStart = {
                update { copy(isLoading = true) }
            },
            onSuccess = { memberInfo ->
                update {
                    copy(
                        nickname = memberInfo.nickname,
                        profileImageUrl = memberInfo.profileImageUrl,
                        avatarColorHex = memberInfo.avatarColorHex,
                        isLoading = false,
                    )
                }
            },
            onFailure = { error ->
                update { copy(isLoading = false) }
                emitEffect(
                    MyPageEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.mypage_error_load,
                            textMessage = { value, resId -> MyPageSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> MyPageSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            getMyInfoUseCase()
        }
    }
}
