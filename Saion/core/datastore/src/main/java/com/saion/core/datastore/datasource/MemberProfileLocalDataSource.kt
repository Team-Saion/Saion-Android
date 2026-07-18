package com.saion.core.datastore.datasource

import com.saion.core.datastore.model.MemberProfileCache

interface MemberProfileLocalDataSource {
    suspend fun getProfile(): MemberProfileCache?

    suspend fun saveProfile(profile: MemberProfileCache)

    suspend fun clearProfile()
}
