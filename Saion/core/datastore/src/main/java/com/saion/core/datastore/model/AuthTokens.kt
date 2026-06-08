package com.saion.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
