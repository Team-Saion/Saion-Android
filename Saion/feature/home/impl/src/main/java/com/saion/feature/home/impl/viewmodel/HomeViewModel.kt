package com.saion.feature.home.impl.viewmodel
import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.circle.ListCirclesUseCase
import com.saion.core.domain.usecase.home.GetHomeUseCase
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class HomeViewModel @Inject constructor(
    private val listCirclesUseCase: ListCirclesUseCase,
    private val getHomeUseCase: GetHomeUseCase,
) : BaseViewModel<HomeState, HomeEffect, HomeIntent>(HomeState.Loading) {
    init {
        dispatch(HomeIntent.Load)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> loadHome()
        }
    }

    private fun loadHome() {
        launchSafely(
            onSuccess = { circles ->
                val firstCircle = circles.firstOrNull() ?: return@launchSafely update { HomeState.None }
                loadHomeOverview(firstCircle.circleId)
            },
            onFailure = { error ->
                update { HomeState.None }
                emitEffect(HomeEffect.ShowSnackbar(error.toSnackbarMessage(R.string.home_error_load_circles)))
            },
        ) {
            listCirclesUseCase()
        }
    }

    private fun loadHomeOverview(circleId: String) {
        launchSafely(
            onSuccess = { overview ->
                update { overview.toUiState() }
            },
            onFailure = { error ->
                update { HomeState.None }
                emitEffect(HomeEffect.ShowSnackbar(error.toSnackbarMessage(R.string.home_error_load_overview)))
            },
        ) {
            getHomeUseCase(circleId = circleId)
        }
    }
}

private fun AppError.toSnackbarMessage(defaultMessageResId: Int): HomeSnackbarMessage = when (this) {
    is AppError.Business -> HomeSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)
    is AppError.Unknown -> HomeSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)
    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> HomeSnackbarMessage.Error(
        error = this,
        defaultMessageResId = defaultMessageResId,
    )
}
