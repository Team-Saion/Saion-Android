package com.saion.core.domain.usecase.circle

import com.saion.core.model.result.AppResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class ObserveResolvedCurrentCircleUseCase @Inject constructor(
    private val observeCurrentCircleUseCase: ObserveCurrentCircleUseCase,
    private val syncCurrentCircleUseCase: SyncCurrentCircleUseCase,
) {
    operator fun invoke(): Flow<ResolvedCurrentCircle> = flow {
        var didAttemptSyncOnMissingCircle = false

        observeCurrentCircleUseCase()
            .distinctUntilChanged()
            .collect { circleId ->
                when {
                    circleId != null -> {
                        didAttemptSyncOnMissingCircle = false
                        emit(ResolvedCurrentCircle.Available(circleId))
                    }

                    didAttemptSyncOnMissingCircle -> emit(ResolvedCurrentCircle.Missing)

                    else -> {
                        didAttemptSyncOnMissingCircle = true
                        when (val result = syncCurrentCircleUseCase()) {
                            is AppResult.Success -> {
                                if (result.data == null) {
                                    emit(ResolvedCurrentCircle.Missing)
                                }
                            }

                            is AppResult.Failure -> emit(ResolvedCurrentCircle.Missing)
                        }
                    }
                }
            }
    }
}

sealed interface ResolvedCurrentCircle {
    data class Available(val circleId: String) : ResolvedCurrentCircle

    data object Missing : ResolvedCurrentCircle
}
