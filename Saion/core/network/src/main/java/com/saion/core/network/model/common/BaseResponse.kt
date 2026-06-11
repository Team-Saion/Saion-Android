package com.saion.core.network.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("data")
    val data: T? = null,
    @SerialName("errorCode")
    val errorCode: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("timestamp")
    val timestamp: String,
)
