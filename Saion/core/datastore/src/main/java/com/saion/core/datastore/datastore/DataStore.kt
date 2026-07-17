package com.saion.core.datastore.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.saion.core.datastore.model.AuthTokens
import com.saion.core.datastore.model.CurrentCircle
import com.saion.core.datastore.model.NotificationSettingCache
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal val Context.authDataStore: DataStore<AuthTokens> by dataStore(
    fileName = "core-local.preferences_pb",
    serializer = object : Serializer<AuthTokens> {
        override suspend fun readFrom(input: InputStream): AuthTokens = Json.decodeFromString<AuthTokens>(input.readBytes().decodeToString())

        override suspend fun writeTo(
            t: AuthTokens,
            output: OutputStream,
        ) {
            withContext(Dispatchers.IO) {
                output.write(Json.encodeToString<AuthTokens>(t).encodeToByteArray())
            }
        }

        override val defaultValue: AuthTokens get() = AuthTokens(accessToken = "", refreshToken = "")
    },
)

internal val Context.currentCircleDataStore: DataStore<CurrentCircle> by dataStore(
    fileName = "current-circle.preferences_pb",
    serializer = object : Serializer<CurrentCircle> {
        override suspend fun readFrom(input: InputStream): CurrentCircle =
            Json.decodeFromString<CurrentCircle>(input.readBytes().decodeToString())

        override suspend fun writeTo(
            t: CurrentCircle,
            output: OutputStream,
        ) {
            withContext(Dispatchers.IO) {
                output.write(Json.encodeToString<CurrentCircle>(t).encodeToByteArray())
            }
        }

        override val defaultValue: CurrentCircle get() = CurrentCircle(selectedCircleId = "")
    },
)

internal val Context.notificationSettingDataStore: DataStore<NotificationSettingCache> by dataStore(
    fileName = "notification-setting.preferences_pb",
    serializer = object : Serializer<NotificationSettingCache> {
        override suspend fun readFrom(input: InputStream): NotificationSettingCache =
            Json.decodeFromString<NotificationSettingCache>(input.readBytes().decodeToString())

        override suspend fun writeTo(
            t: NotificationSettingCache,
            output: OutputStream,
        ) {
            withContext(Dispatchers.IO) {
                output.write(Json.encodeToString<NotificationSettingCache>(t).encodeToByteArray())
            }
        }

        override val defaultValue: NotificationSettingCache
            get() = NotificationSettingCache(
                hasValue = false,
                d7Enabled = false,
                d1Enabled = false,
                ddayEnabled = false,
                familyScheduleCheckEnabled = false,
            )
    },
)
