package com.saion.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.saion.app.ui.SaionApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureEdgeToEdge()
        setContent {
            SaionApp()
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
