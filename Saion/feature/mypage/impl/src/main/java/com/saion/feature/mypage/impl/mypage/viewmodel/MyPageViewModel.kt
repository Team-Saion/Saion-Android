package com.saion.feature.mypage.impl.mypage.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.member.LogoutUseCase
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.mypage.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class MyPageViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
) :
    BaseViewModel<MyPageState, MyPageEffect, MyPageIntent>(MyPageState()) {
    init {
        loadMyInfo()
    }

    override fun handleIntent(intent: MyPageIntent) {
        when (intent) {
            MyPageIntent.ClickLogout -> update { copy(showLogoutDialog = true) }
            MyPageIntent.DismissLogoutDialog -> update { copy(showLogoutDialog = false, isLogoutLoading = false) }
            MyPageIntent.ConfirmLogout -> {
                if (currentState.isLogoutLoading) return
                logout()
            }
        }
    }

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

    private fun logout() {
        launchSafely(
            onStart = {
                update {
                    copy(
                        showLogoutDialog = true,
                        isLogoutLoading = true,
                    )
                }
            },
            onSuccess = {
                update {
                    copy(
                        showLogoutDialog = false,
                        isLogoutLoading = false,
                    )
                }
                emitEffect(MyPageEffect.LogoutCompleted)
            },
            onFailure = { error ->
                update { copy(isLogoutLoading = false) }
                emitEffect(
                    MyPageEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.mypage_error_logout,
                            textMessage = { value, resId -> MyPageSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> MyPageSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            logoutUseCase()
        }
    }
}
