package com.saion.core.data.repository

import com.saion.core.domain.repository.CoreRepository
import com.saion.core.local.datasource.CoreLocalDataSource
import com.saion.core.network.datasource.AuthRemoteDataSource
import javax.inject.Inject

class CoreRepositoryImpl @Inject constructor(
    val localSource: CoreLocalDataSource,
    val remoteSource: AuthRemoteDataSource,
) : CoreRepository
