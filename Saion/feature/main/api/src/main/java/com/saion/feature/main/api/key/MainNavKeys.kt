package com.saion.feature.main.api.key

import com.saion.core.navigation.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class MainNavKey(
    val sessionId: Long = DEFAULT_SESSION_ID,
) : AppNavKey {
    companion object {
        const val DEFAULT_SESSION_ID: Long = 0L

        fun fresh(): MainNavKey = MainNavKey(sessionId = System.nanoTime())
    }
}

interface MainTabNavKey : AppNavKey
