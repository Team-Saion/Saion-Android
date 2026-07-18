package com.saion.core.domain.repository

import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeHome(circleId: String): Flow<HomeOverview?>

    fun observeMembers(circleId: String): Flow<List<CircleMember>>

    suspend fun getHome(circleId: String): AppResult<HomeOverview>

    suspend fun refreshHome(circleId: String): AppResult<HomeOverview>

    suspend fun getMembers(circleId: String): AppResult<List<CircleMember>>

    suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: MemberInfo,
    )
}
