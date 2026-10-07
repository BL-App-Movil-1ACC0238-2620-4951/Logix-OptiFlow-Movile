package com.logix.optiflow.domain.model

import java.util.UUID

data class Patient(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,
)

data class PatientSession(
    val patientId: UUID,
    val token: String?,
    val name: String,
    val email: String,
)
