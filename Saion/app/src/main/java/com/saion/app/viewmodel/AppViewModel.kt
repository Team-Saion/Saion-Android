package com.saion.app.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saion.app.navigation.startup.AppStartDestination
import com.saion.core.domain.usecase.auth.ClearSessionUseCase
import com.saion.core.domain.usecase.auth.GetStoredMemberRoleUseCase
import com.saion.core.domain.usecase.auth.IsSignedInUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.notification.NotificationLifecycleManager
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
    private val getStoredMemberRoleUseCase: GetStoredMemberRoleUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val syncCurrentCircleUseCase: SyncCurrentCircleUseCase,
    private val notificationLifecycleManager: NotificationLifecycleManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val startDestination = resolveStartDestination()
            if (startDestination != AppStartDestination.SplashThenLogin) {
                launch { notificationLifecycleManager.syncOnAppLaunchIfSignedIn() }
            }
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

        return when (val result = getStoredMemberRoleUseCase()) {
            is AppResult.Success -> when (result.data) {
                MemberRole.PENDING -> AppStartDestination.Auth(startStep = AuthStartStep.TERMS)

                MemberRole.MEMBER,
                MemberRole.ADMIN,
                -> {
                    syncCurrentCircleUseCase()
                    AppStartDestination.Main
                }
            }

            is AppResult.Failure -> {
                clearSessionUseCase()
                AppStartDestination.SplashThenLogin
            }
        }
    }
}
