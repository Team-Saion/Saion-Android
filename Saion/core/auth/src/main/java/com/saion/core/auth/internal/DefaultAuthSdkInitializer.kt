package com.saion.core.auth.internal

import com.saion.core.auth.AuthSdkInitializer
import javax.inject.Inject

internal class DefaultAuthSdkInitializer @Inject constructor(private val initializers: Set<@JvmSuppressWildcards ProviderSdkInitializer>) :
    AuthSdkInitializer {
    override fun initialize() {
        initializers.forEach(ProviderSdkInitializer::initialize)
    }
}
