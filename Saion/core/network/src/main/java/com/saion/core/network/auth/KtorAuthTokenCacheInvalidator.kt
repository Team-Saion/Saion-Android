package com.saion.core.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.clearAuthTokens
import javax.inject.Inject

internal class KtorAuthTokenCacheInvalidator @Inject constructor(
    private val httpClient: HttpClient,
) : AuthTokenCacheInvalidator {
    override fun invalidate() {
        httpClient.clearAuthTokens()
    }
}
