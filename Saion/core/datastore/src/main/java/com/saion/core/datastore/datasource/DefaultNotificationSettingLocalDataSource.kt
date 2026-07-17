package com.saion.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.saion.core.datastore.model.NotificationSettingCache
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

internal class DefaultNotificationSettingLocalDataSource @Inject constructor(
    private val dataStore: DataStore<NotificationSettingCache>,
) : NotificationSettingLocalDataSource {
    override suspend fun getSetting(): NotificationSettingCache? = dataStore.data.firstOrNull()?.takeIf { it.hasValue }

    override suspend fun saveSetting(setting: NotificationSettingCache) {
        dataStore.updateData { setting.copy(hasValue = true) }
    }

    override suspend fun clearSetting() {
        dataStore.updateData {
            NotificationSettingCache(
                hasValue = false,
                d7Enabled = false,
                d1Enabled = false,
                ddayEnabled = false,
                familyScheduleCheckEnabled = false,
            )
        }
    }
}
