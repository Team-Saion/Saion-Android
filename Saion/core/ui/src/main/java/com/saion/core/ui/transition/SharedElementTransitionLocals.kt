package com.saion.core.ui.transition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSharedElementTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

val LocalSharedElementAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope?> { null }
