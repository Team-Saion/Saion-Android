package com.saion.core.datastore.datasource

import com.saion.core.datastore.model.NotificationSettingCache

interface NotificationSettingLocalDataSource {
    suspend fun getSetting(): NotificationSettingCache?

    suspend fun saveSetting(setting: NotificationSettingCache)

    suspend fun clearSetting()
}
