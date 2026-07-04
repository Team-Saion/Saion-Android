package com.saion.app.viewmodel

import com.saion.app.navigation.startup.AppStartDestination
import androidx.compose.runtime.Stable
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
    val startDestination: AppStartDestination? = null,
)

@HiltViewModel
@Stable
class AppViewModel @Inject constructor(
    private val isSignedInUseCase: IsSignedInUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val startDestination = resolveStartDestination()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    startDestination = startDestination,
                )
            }
        }
    }

    private suspend fun resolveStartDestination(): AppStartDestination {
        if (!isSignedInUseCase()) {
            return AppStartDestination.SplashThenLogin
        }

        return when (val result = getMyInfoUseCase()) {
            is AppResult.Success -> when (result.data.role) {
                MemberRole.PENDING -> AppStartDestination.Auth(startStep = AuthStartStep.TERMS)

                MemberRole.MEMBER,
                MemberRole.ADMIN,
                -> AppStartDestination.Main
            }

            is AppResult.Failure -> AppStartDestination.SplashThenLogin
        }
    }
}
