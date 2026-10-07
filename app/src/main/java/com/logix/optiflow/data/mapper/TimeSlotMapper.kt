package com.logix.optiflow.data.mapper

import com.logix.optiflow.data.remote.dto.TimeSlotDto
import com.logix.optiflow.domain.model.TimeSlot

fun TimeSlotDto.toDomain(): TimeSlot =
    TimeSlot(
        id = id,
        opticalStoreId = opticalStoreId,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        status = status,
    )
