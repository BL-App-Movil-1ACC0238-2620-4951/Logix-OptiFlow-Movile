package com.logix.optiflow.data.remote.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

class UuidJsonAdapter {
    @FromJson
    fun fromJson(value: String): UUID = UUID.fromString(value)

    @ToJson
    fun toJson(value: UUID): String = value.toString()
}
