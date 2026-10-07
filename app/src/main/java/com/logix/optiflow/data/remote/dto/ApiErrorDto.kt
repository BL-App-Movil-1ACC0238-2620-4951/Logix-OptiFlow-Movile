package com.logix.optiflow.data.remote.dto

data class ApiErrorDto(
    val status: Int?,
    val error: String?,
    val message: String?,
)
