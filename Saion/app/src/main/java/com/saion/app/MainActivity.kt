package com.saion.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.saion.app.ui.SaionApp
import com.saion.app.viewmodel.AppViewModel
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.collections.immutable.toImmutableSet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var rootEntryBuilders: Set<@JvmSuppressWildcards NavEntryBuilder<AppNavKey>>

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureEdgeToEdge()
        setContent {
            SaionApp(
                appViewModel = appViewModel,
                rootEntryBuilders = rootEntryBuilders.toImmutableSet(),
            )
        }
    }

    private fun configureEdgeToEdge() {
        val scrim = SystemBarStyle.light(
            scrim = Color.Transparent.toArgb(),
            darkScrim = Color.Transparent.toArgb(),
        )
        enableEdgeToEdge(statusBarStyle = scrim, navigationBarStyle = scrim)
    }
}
