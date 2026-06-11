package com.saion.core.ui.viewmodel

import android.util.Log
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    private val logTree = RecordingTree()
    private val viewModel = TestBaseViewModel(logTree)

    @Test
    fun `인증 만료 에러는 세션 만료 이벤트만 전파한다`() = runTest {
        logTree.clear()
        val event = async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.Unauthorized()).join()

        assertEquals(GlobalUiEvent.SessionExpired, event.await())
        assertLog(
            priority = Log.WARN,
            messagePart = "인증 만료 에러:",
        )
    }

    @Test
    fun `네트워크 연결 불가 에러는 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val cause = java.io.IOException("offline")
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.NetworkUnavailable(cause = cause)).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.WARN,
            messagePart = "네트워크 연결 불가 에러:",
            throwable = cause,
        )
    }

    @Test
    fun `타임아웃 에러는 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val cause = java.net.SocketTimeoutException("timeout")
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.Timeout(cause = cause)).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.WARN,
            messagePart = "요청 시간 초과 에러:",
            throwable = cause,
        )
    }

    @Test
    fun `알 수 없는 에러는 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.Unknown(message = "Forbidden")).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.ERROR,
            messagePart = "알 수 없는 에러: Forbidden",
        )
    }

    @Test
    fun `서비스 불가 에러는 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.ServerUnavailable()).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.ERROR,
            messagePart = "서비스 불가 에러:",
        )
    }

    @Test
    fun `비즈니스 에러는 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(
            AppError.Business(
                businessType = BusinessErrorType.DUPLICATE,
                rawCode = "M409_1",
                message = "Email already exists",
            ),
        ).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.INFO,
            messagePart = "비즈니스 에러: businessType=DUPLICATE, rawCode=M409_1, message=Email already exists",
        )
    }

    @Test
    fun `원인이 있는 알 수 없는 에러도 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val cause = IllegalStateException("boom")
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(AppError.Unknown(cause = cause)).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.ERROR,
            messagePart = "알 수 없는 에러: null",
            throwable = cause,
        )
    }

    @Test
    fun `메시지가 없는 비즈니스 에러도 전역 이벤트를 전파하지 않는다`() = runTest {
        logTree.clear()
        val event = backgroundScope.async { GlobalUiEventBus.events.first() }

        viewModel.publishError(
            AppError.Business(
                businessType = BusinessErrorType.DUPLICATE,
                rawCode = "M409_1",
                message = null,
            ),
        ).join()

        advanceUntilIdle()
        assertTrue(!event.isCompleted)
        event.cancel()
        assertLog(
            priority = Log.INFO,
            messagePart = "비즈니스 에러: businessType=DUPLICATE, rawCode=M409_1, message=null",
        )
    }

    @Test
    fun `이펙트를 발행하면 효과 흐름으로 전달된다`() = runTest {
        logTree.clear()
        val effect = TestEffect.ShowToast("ignored")

        viewModel.publishEffect(effect)

        assertEquals(
            effect,
            viewModel.uiEffect.first(),
        )
    }

    @Test
    fun `인텐트를 dispatch 하면 추적 로그를 남긴다`() = runTest {
        logTree.clear()
        val intentViewModel = IntentTestBaseViewModel(logTree)
        advanceUntilIdle()

        intentViewModel.dispatch(IntentTestIntent.Crash)
        intentViewModel.dispatch(IntentTestIntent.Record)
        advanceUntilIdle()

        assertTrue(intentViewModel.handledIntents.isEmpty())
        assertEquals(2, logTree.entries.size)
        assertEquals(Log.VERBOSE, logTree.entries[0].priority)
        assertTrue(logTree.entries[0].message.contains("Dispatch intent: Crash"))
        assertEquals(Log.VERBOSE, logTree.entries[1].priority)
        assertTrue(logTree.entries[1].message.contains("Dispatch intent: Record"))
    }

    private fun assertLog(
        priority: Int,
        messagePart: String,
        throwable: Throwable? = null,
    ) {
        assertEquals(1, logTree.entries.size)
        val entry = logTree.entries.single()
        assertEquals(priority, entry.priority)
        assertTrue(entry.message.contains(messagePart))
        if (throwable == null) {
            assertNull(entry.throwable)
        } else {
            assertEquals(throwable, entry.throwable)
        }
    }
}

private data object TestState : UIState

private data object TestIntent : UIIntent

private sealed interface TestEffect : UIEffect {
    data class ShowToast(val message: String) : TestEffect
}

private sealed interface IntentTestIntent : UIIntent {
    data object Crash : IntentTestIntent

    data object Record : IntentTestIntent
}

private class TestBaseViewModel(override val logger: Timber.Tree) : BaseViewModel<TestState, TestEffect, TestIntent>(TestState) {
    override fun handleIntent(intent: TestIntent) = Unit

    fun publishError(error: AppError): Job = launchSafely(
        onSuccess = {},
    ) {
        AppResult.Failure(error)
    }

    suspend fun publishEffect(effect: TestEffect) {
        emitEffect(effect)
    }
}

private class IntentTestBaseViewModel(override val logger: Timber.Tree) : BaseViewModel<TestState, TestEffect, IntentTestIntent>(TestState) {
    val crashError = IllegalStateException("boom")
    val handledIntents = mutableListOf<IntentTestIntent>()

    override fun handleIntent(intent: IntentTestIntent) {
        when (intent) {
            IntentTestIntent.Crash -> throw crashError
            IntentTestIntent.Record -> handledIntents += intent
        }
    }
}

private class RecordingTree : Timber.Tree() {
    val entries = mutableListOf<LogEntry>()

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        entries += LogEntry(priority = priority, message = message, throwable = t)
    }

    fun clear() {
        entries.clear()
    }
}

private data class LogEntry(
    val priority: Int,
    val message: String,
    val throwable: Throwable?,
)
