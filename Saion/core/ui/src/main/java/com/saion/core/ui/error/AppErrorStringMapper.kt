package com.saion.core.ui.error

import android.content.Context
import android.content.res.Resources
import com.saion.core.model.result.AppError
import com.saion.core.ui.R

fun Resources.getString(error: AppError): String = when (error) {
    is AppError.NetworkUnavailable -> getString(R.string.error_network_unavailable)
    is AppError.Timeout -> getString(R.string.error_timeout)
    is AppError.Unauthorized -> getString(R.string.error_unauthorized)
    is AppError.ServerUnavailable -> getString(R.string.error_server_unavailable)
    is AppError.Unknown -> error.message ?: getString(R.string.error_unknown)
    is AppError.Business -> error("비즈니스 에러는 별도의 처리를 해야합니다.")
}

fun Context.getString(error: AppError): String = resources.getString(error)
