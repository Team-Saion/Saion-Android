package com.saion.feature.auth.api.key

import kotlinx.serialization.Serializable

@Serializable
enum class AuthStartStep {
    LOGIN,
    TERMS,
}
