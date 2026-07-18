package com.saion.core.notification

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FcmTokenProvider @Inject constructor() {
    suspend fun getToken(): String = FirebaseMessaging.getInstance().token.await()
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        when {
            task.isSuccessful -> continuation.resume(task.result)
            task.exception != null -> continuation.resumeWithException(task.exception!!)
            else -> continuation.resumeWithException(IllegalStateException("Task completed without result."))
        }
    }
}
