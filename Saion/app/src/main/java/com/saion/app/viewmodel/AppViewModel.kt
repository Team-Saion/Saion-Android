package com.saion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.auth.IsSignedInUseCase
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import com.saion.feature.auth.api.key.AuthStartStep
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val isLoading: Boolean = true,
    val rootTarget: RootNavigationTarget? = null,
)

sealed interface RootNavigationTarget {
    data class Auth(val startStep: AuthStartStep) : RootNavigationTarget

    data object Main : RootNavigationTarget
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val isSignedInUseCase: IsSignedInUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val target = resolveStartTarget()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    rootTarget = target,
                )
            }
        }
    }

    private suspend fun resolveStartTarget(): RootNavigationTarget {
        if (!isSignedInUseCase()) {
            return RootNavigationTarget.Auth(startStep = AuthStartStep.LOGIN)
        }

        return when (val result = getMyInfoUseCase()) {
            is AppResult.Success -> when (result.data.role) {
                MemberRole.PENDING -> RootNavigationTarget.Auth(startStep = AuthStartStep.TERMS)

                MemberRole.MEMBER,
                MemberRole.ADMIN,
                -> RootNavigationTarget.Main
            }

            is AppResult.Failure -> RootNavigationTarget.Auth(startStep = AuthStartStep.LOGIN)
        }
    }
}
