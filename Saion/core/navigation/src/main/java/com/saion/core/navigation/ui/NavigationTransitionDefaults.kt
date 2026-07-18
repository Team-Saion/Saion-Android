package com.saion.core.navigation.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

private const val NAVIGATION_FADE_DURATION_MILLIS = 220

internal fun <T : Any> defaultNavigationTransitionSpec():
    AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    fadeIn(animationSpec = tweenSpec()) togetherWith ExitTransition.None
}

internal fun <T : Any> defaultNavigationPopTransitionSpec():
    AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    EnterTransition.None togetherWith fadeOut(animationSpec = tweenSpec())
}

internal fun <T : Any> defaultNavigationPredictivePopTransitionSpec():
    AnimatedContentTransitionScope<Scene<T>>.(
        @NavigationEvent.SwipeEdge Int
    ) -> ContentTransform = {
        EnterTransition.None togetherWith fadeOut(animationSpec = tweenSpec())
    }

private fun tweenSpec() = tween<Float>(
    durationMillis = NAVIGATION_FADE_DURATION_MILLIS,
)
