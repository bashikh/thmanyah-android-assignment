package com.thmanyah.shasha.core.util

import androidx.annotation.StringRes
import com.thmanyah.shasha.R
import retrofit2.HttpException
import java.io.IOException

@StringRes
fun Throwable.toErrorStringRes(): Int = when (this) {
    is IOException -> R.string.error_network
    is HttpException -> R.string.error_server
    else -> R.string.error_unknown
}
