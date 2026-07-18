package com.saion.core.ui.component

import androidx.compose.material3.SnackbarDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun `visuals defaults variant to null`() {
        val visuals = SaionSnackbarVisuals(message = "Toast")

        assertEquals("Toast", visuals.message)
        assertNull(visuals.variant)
        assertEquals(SnackbarDuration.Short, visuals.duration)
    }
}
