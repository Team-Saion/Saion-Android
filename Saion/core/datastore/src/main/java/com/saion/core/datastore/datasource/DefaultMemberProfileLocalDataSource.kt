package com.saion.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.saion.core.datastore.model.MemberProfileCache
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

internal class DefaultMemberProfileLocalDataSource @Inject constructor(
    private val dataStore: DataStore<MemberProfileCache>,
) : MemberProfileLocalDataSource {
    override suspend fun getProfile(): MemberProfileCache? = dataStore.data.firstOrNull()?.takeIf { it.hasValue }

    override suspend fun saveProfile(profile: MemberProfileCache) {
        dataStore.updateData { profile.copy(hasValue = true) }
    }

    override suspend fun clearProfile() {
        dataStore.updateData {
            MemberProfileCache(
                hasValue = false,
                nickname = "",
                profileImageUrl = "",
                avatarColorHex = "",
                role = "",
                status = "",
            )
        }
    }
}
