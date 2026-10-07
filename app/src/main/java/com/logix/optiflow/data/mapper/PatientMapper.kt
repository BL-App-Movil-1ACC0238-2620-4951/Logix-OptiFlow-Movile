package com.logix.optiflow.data.mapper

import com.logix.optiflow.data.remote.dto.PatientDto
import com.logix.optiflow.domain.model.Patient

fun PatientDto.toDomain(): Patient =
    Patient(
        id = id,
        name = name,
        email = email,
        phone = phone,
    )
