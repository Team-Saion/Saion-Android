package com.saion.core.ui.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaionSnackbarVisualsTest {
    @Test
    fun `visuals stores provided variant and duration`() {
        val visuals = SaionSnackbarVisuals(
            message = "Toast",
            variant = SaionSnackbarVariant.Positive,
            duration = SnackbarDuration.Long,
        )

        assertEquals("Toast", visuals.message)
        assertEquals(SaionSnackbarVariant.Positive, visuals.variant)
        assertEquals(SnackbarDuration.Long, visuals.duration)
    }

    @Test
    fun `visuals stores provided action label`() {
        val visuals = SaionSnackbarVisuals(
            message = "Toast",
            actionLabel = "Retry",
        )

        assertEquals("Retry", visuals.actionLabel)
    }

    @Test
    fun `visuals defaults variant to null`() {
        val visuals = SaionSnackbarVisuals(message = "Toast")

        assertEquals("Toast", visuals.message)
        assertNull(visuals.variant)
        assertEquals(SnackbarDuration.Short, visuals.duration)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `showSaionSnackbar invokes action callback when action is performed`() = runTest {
        val hostState = SnackbarHostState()
        var dismissed = false
        var actionPerformed = false

        val deferred = async {
            hostState.showSaionSnackbar(
                message = "Toast",
                actionLabel = "Retry",
                duration = SnackbarDuration.Indefinite,
                onDismiss = { dismissed = true },
                onActionPerform = { actionPerformed = true },
            )
        }

        advanceUntilIdle()
        hostState.currentSnackbarData?.performAction()
        deferred.await()

        assertTrue(actionPerformed)
        assertFalse(dismissed)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `showSaionSnackbar invokes dismiss callback when dismissed`() = runTest {
        val hostState = SnackbarHostState()
        var dismissed = false
        var actionPerformed = false

        val deferred = async {
            hostState.showSaionSnackbar(
                message = "Toast",
                actionLabel = "Retry",
                duration = SnackbarDuration.Indefinite,
                onDismiss = { dismissed = true },
                onActionPerform = { actionPerformed = true },
            )
        }

        advanceUntilIdle()
        hostState.currentSnackbarData?.dismiss()
        deferred.await()

        assertTrue(dismissed)
        assertFalse(actionPerformed)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `showSaionSnackbar removes trailing period from message`() = runTest {
        val hostState = SnackbarHostState()

        val deferred = async {
            hostState.showSaionSnackbar(
                message = "Toast.",
                duration = SnackbarDuration.Indefinite,
            )
        }

        advanceUntilIdle()
        assertEquals("Toast", hostState.currentSnackbarData?.visuals?.message)
        hostState.currentSnackbarData?.dismiss()
        deferred.await()
    }
}
