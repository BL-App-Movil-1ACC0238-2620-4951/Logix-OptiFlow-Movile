package com.logix.optiflow.data.mapper

import com.logix.optiflow.data.remote.dto.AppointmentDto
import com.logix.optiflow.domain.model.Appointment

fun AppointmentDto.toDomain(): Appointment =
    Appointment(
        id = id,
        patientId = patientId,
        opticalStoreId = opticalStoreId,
        timeSlotId = timeSlotId,
        status = status,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
    )
