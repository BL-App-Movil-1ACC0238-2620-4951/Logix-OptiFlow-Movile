package com.logix.optiflow.data.remote

import com.logix.optiflow.data.remote.dto.ApiErrorDto
import com.logix.optiflow.di.NetworkModule
import retrofit2.HttpException

fun Throwable.toUserMessage(): String {
    if (this is HttpException) {
        val body = response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            runCatching {
                NetworkModule.moshi().adapter(ApiErrorDto::class.java).fromJson(body)
            }.getOrNull()?.message?.takeIf { it.isNotBlank() }?.let { return it }
        }
        return message ?: "Request failed with code ${code()}"
    }
    return message ?: "Unexpected error"
}
