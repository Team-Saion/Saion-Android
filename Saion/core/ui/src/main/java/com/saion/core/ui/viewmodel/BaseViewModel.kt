package com.saion.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

interface UIState

interface UIIntent

interface UIEffect

/**
 * 단일 화면의 상태, effect, intent 흐름을 관리하는 기본 ViewModel입니다.
 *
 * - 화면 상태는 [uiState]로 노출됩니다.
 * - one-shot effect는 [uiEffect]로 전달됩니다.
 * - 외부에서는 [dispatch]로 intent를 전달합니다.
 * - 하위 클래스는 [handleIntent]에서 각 intent를 처리합니다.
 *
 * [launchSafely]는 [AppResult] 처리, 공통 에러 로깅, 예외 처리를 묶어줍니다.
 *
 * 기본 [handleError]는 인증 만료처럼 화면 전역에서 공통으로 처리해야 하는 에러만 다룹니다.
 *
 * 화면별 사용자 안내, 재시도, 스낵바, 네비게이션 처리는 하위 ViewModel에서 직접 결정합니다.
 */
abstract class BaseViewModel<STATE : UIState, EFFECT : UIEffect, INTENT : UIIntent>(initialState: STATE) : ViewModel() {
    protected open val logger: Timber.Tree = Timber.tag(this::class.simpleName ?: "BaseViewModel")

    private val _uiState = MutableStateFlow(initialState)

    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    protected val currentState: STATE
        get() = uiState.value

    private val _uiEffect = Channel<EFFECT>(capacity = Channel.BUFFERED)

    val uiEffect: Flow<EFFECT> = _uiEffect.receiveAsFlow()

    private val _uiIntent = Channel<INTENT>(capacity = Channel.BUFFERED)
    private val uiIntent: Flow<INTENT> = _uiIntent.receiveAsFlow()

    init {
        viewModelScope.launch {
            uiIntent.collect { intent ->
                try {
                    handleIntent(intent)
                } catch (ce: CancellationException) {
                    throw ce
                } catch (throwable: Throwable) {
                    logger.e(t = throwable, message = "Unhandled error while handling intent: $intent")
                    handleError(AppError.Unknown(cause = throwable))
                }
            }
        }
    }

    fun dispatch(intent: INTENT) {
        _uiIntent.trySend(intent)
            .onSuccess { logger.v("Dispatch intent: $intent") }
            .onFailure { throwable -> logger.w(t = throwable, message = "Intent 유실: $intent, 원인=${throwable?.message}") }
    }

    protected abstract fun handleIntent(intent: INTENT)

    protected fun update(block: STATE.() -> STATE) {
        _uiState.update(block)
    }

    protected suspend fun emitEffect(effect: EFFECT) {
        _uiEffect.send(effect)
    }

    /**
     * 공통 시작/종료 처리와 [AppResult] 분기를 포함해 비동기 작업을 실행합니다.
     *
     * [AppResult.Failure]는 [handleError]로 전달됩니다.
     *
     * [block] 내부에서 예상하지 못한 예외가 발생하면 [AppError.Unknown]으로 변환해 [handleError]에 전달합니다.
     */
    protected fun <DATA> launchSafely(
        onStart: () -> Unit = { },
        onSuccess: suspend (DATA) -> Unit,
        onFailure: suspend (AppError) -> Unit = {},
        onFinally: () -> Unit = {},
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> AppResult<DATA>,
    ): Job = viewModelScope.launch(context) {
        try {
            onStart()
            block().handleApiResult(onSuccess = onSuccess, onFailure = onFailure)
        } catch (ce: CancellationException) {
            throw ce
        } catch (throwable: Throwable) {
            logger.e(t = throwable, message = "Unhandled error while running task")
            handleError(AppError.Unknown(cause = throwable))
        } finally {
            onFinally()
        }
    }

    private suspend fun <DATA> AppResult<DATA>.handleApiResult(
        onSuccess: suspend (DATA) -> Unit,
        onFailure: suspend (AppError) -> Unit,
    ) {
        when (this) {
            is AppResult.Success -> onSuccess(data)

            is AppResult.Failure -> {
                handleError(error = error)
                onFailure(error)
            }
        }
    }

    /**
     * 공통 에러 로깅과 전역 후처리를 수행합니다.
     *
     * 기본 구현은 [AppError.Unauthorized]를 전역 세션 만료 이벤트로 전파합니다.
     * 화면별 사용자 안내나 재시도 UI는 하위 클래스에서 override하거나 별도 effect로 처리합니다.
     */
    private fun handleError(error: AppError) {
        when (error) {
            is AppError.NetworkUnavailable -> {
                logger.w(t = error.cause, message = "네트워크 연결 불가 에러: ${error.cause?.message}")
            }

            is AppError.Timeout -> {
                logger.w(t = error.cause, message = "요청 시간 초과 에러: ${error.cause?.message}")
            }

            is AppError.Unauthorized -> {
                logger.w(t = error.cause, message = "인증 만료 에러: ${error.cause?.message}")
                GlobalUiEventBus.emit(GlobalUiEvent.SessionExpired)
            }

            is AppError.ServerUnavailable -> {
                logger.e(t = error.cause, message = "서비스 불가 에러: ${error.cause?.message}")
            }

            is AppError.Business -> {
                logger.i(
                    t = error.cause,
                    message = "비즈니스 에러: businessType=${error.businessType}, rawCode=${error.rawCode}, message=${error.message}",
                )
            }

            is AppError.Unknown -> {
                logger.e(t = error.cause, message = "알 수 없는 에러: ${error.message}")
            }
        }
    }
}
