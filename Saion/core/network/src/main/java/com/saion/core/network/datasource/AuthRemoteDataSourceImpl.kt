package com.saion.core.network.datasource

import com.saion.core.network.api.AuthService
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(private val service: AuthService) : AuthRemoteDataSource
