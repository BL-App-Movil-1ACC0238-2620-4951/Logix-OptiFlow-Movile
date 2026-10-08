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
    val raw = message.orEmpty()
    if (
        raw.contains("10.0.2.2", ignoreCase = true) ||
        raw.contains("failed to connect", ignoreCase = true) ||
        raw.contains("Unable to resolve host", ignoreCase = true)
    ) {
        return "No se pudo conectar al servidor. Si usas la variante local, levanta el backend en tu PC (puerto 8080). " +
            "Si no, en Android Studio elige Build Variants → prodDebug para usar Render en la nube."
    }
    return raw.ifBlank { "Error inesperado. Intenta de nuevo." }
}
