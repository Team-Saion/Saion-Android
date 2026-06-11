package com.saion.core.network.client

import android.os.Build
import com.saion.core.network.BuildConfig
import com.saion.core.network.auth.AuthTokenStore
import com.saion.core.network.datasource.AuthRemoteDataSource
import dagger.Lazy
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.clearAuthTokens
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Inject
import kotlinx.serialization.SerializationException

internal class HttpClientFactory @Inject constructor(
    private val authTokenStore: AuthTokenStore,
    private val authRemoteDataSource: Lazy<AuthRemoteDataSource>,
    private val appPackageInfoProvider: AppPackageInfoProvider,
) {
    fun create(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient(OkHttp) {
        expectSuccess = false
        installBaseConfiguration()
        block()
    }

    private fun HttpClientConfig<*>.installBaseConfiguration() {
        configureDefaultRequest()
        installContentNegotiation()
        installHttpRequestRetry()
        installUserAgent()
        installLogging()
        installAuth(
            authTokenStore = authTokenStore,
            authRemoteDataSource = authRemoteDataSource,
        )
    }

    private fun HttpClientConfig<*>.configureDefaultRequest() {
        defaultRequest {
            url(BuildConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }

    private fun HttpClientConfig<*>.installContentNegotiation() {
        install(ContentNegotiation) {
            json(NetworkJson.instance)
        }
    }

    private fun HttpClientConfig<*>.installHttpRequestRetry() {
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response -> response.status.value in 500..599 }
            retryOnExceptionIf { _, cause -> cause is java.io.IOException }
            exponentialDelay()
        }
    }

    // Example: Saion-Android/1.0.0 (1; release; Android 14; Google Pixel 8)
    private fun HttpClientConfig<*>.installUserAgent() {
        install(UserAgent) {
            agent = buildString {
                append("Saion-Android/")
                append(appPackageInfoProvider.versionName())
                append(" (")
                append(appPackageInfoProvider.versionCode())
                append("; ")
                append(BuildConfig.BUILD_TYPE)
                append("; Android ")
                append(Build.VERSION.RELEASE ?: "unknown")
                append("; ")
                append(deviceName())
                append(")")
            }
        }
    }

    private fun HttpClientConfig<*>.installLogging() {
        install(Logging) {
            logger = KtorPrettyLogger
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    private fun HttpClientConfig<*>.installAuth(
        authTokenStore: AuthTokenStore,
        authRemoteDataSource: Lazy<AuthRemoteDataSource>,
    ) {
        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = authTokenStore.getAccessToken()
                    val refreshToken = authTokenStore.getRefreshToken()
                    if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                        null
                    } else {
                        BearerTokens(accessToken = accessToken, refreshToken = refreshToken)
                    }
                }

                sendWithoutRequest { request -> request.isAuthRequest().not() }

                refreshTokens {
                    val refreshToken = authTokenStore.getRefreshToken()
                        ?: return@refreshTokens client.clearTokensAndReturnNull(authTokenStore)

                    runCatching {
                        authRemoteDataSource.get().refreshToken(refreshToken = refreshToken)
                    }.fold(
                        onSuccess = { response ->
                            if (!response.isSuccess) {
                                if (response.statusCode in listOf(401, 403)) {
                                    return@fold client.clearTokensAndReturnNull(authTokenStore)
                                }
                                return@fold null
                            }

                            val tokenData = response.data ?: return@fold null
                            authTokenStore.saveTokens(
                                accessToken = tokenData.accessToken,
                                refreshToken = tokenData.refreshToken,
                            )
                            BearerTokens(
                                accessToken = tokenData.accessToken,
                                refreshToken = tokenData.refreshToken,
                            )
                        },
                        onFailure = { throwable ->
                            if (throwable is SerializationException) {
                                throw throwable
                            }
                            null
                        },
                    )
                }
            }
        }
    }

    private suspend fun HttpClient.clearTokensAndReturnNull(authTokenStore: AuthTokenStore): BearerTokens? {
        authTokenStore.clearTokens()
        clearAuthTokens()
        return null
    }

    private fun HttpRequestBuilder.isAuthRequest(): Boolean {
        val encodedPath = url.build().encodedPath
        return encodedPath.contains("/api/v1/auth")
    }

    private fun deviceName(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().trim()
        val model = Build.MODEL.orEmpty().trim()
        if (manufacturer.isEmpty()) return model.ifEmpty { "unknown" }
        if (model.isEmpty()) return manufacturer
        return if (model.startsWith(manufacturer, ignoreCase = true)) model else "$manufacturer $model"
    }
}
