package com.saion.core.domain.repository

import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppResult

interface HomeRepository {
    suspend fun getHome(circleId: String): AppResult<HomeOverview>

    suspend fun getMembers(circleId: String): AppResult<List<CircleMember>>
}
