package com.saion.core.network.client

import kotlinx.serialization.json.Json

internal object NetworkJson {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
}
