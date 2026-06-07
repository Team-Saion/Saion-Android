package com.saion.core.local.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class CorePreferenceLocalDataSource
@Inject
constructor(val dataStore: DataStore<Preferences>) : CoreLocalDataSource
