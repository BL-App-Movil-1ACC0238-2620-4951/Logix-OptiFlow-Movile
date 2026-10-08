package com.logix.optiflow.data.mapper

import com.logix.optiflow.data.remote.dto.OpticalStoreDto
import com.logix.optiflow.domain.model.OpticalStore

fun OpticalStoreDto.toDomain(): OpticalStore =
    OpticalStore(
        id = id,
        name = name,
        address = address,
        phone = phone,
        rating = rating,
        status = status,
    )
