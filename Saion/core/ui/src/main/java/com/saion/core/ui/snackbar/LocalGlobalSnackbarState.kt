package com.saion.core.ui.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

val LocalGlobalSnackbarState = staticCompositionLocalOf<SnackbarHostState> { error("No GlobalSnackbarState provided") }
