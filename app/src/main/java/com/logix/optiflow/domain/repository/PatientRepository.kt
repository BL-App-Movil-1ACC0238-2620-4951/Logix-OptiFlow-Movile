package com.logix.optiflow.domain.repository

import com.logix.optiflow.domain.model.Patient
import com.logix.optiflow.domain.model.PatientSession

interface PatientRepository {
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
    ): Patient

    suspend fun login(
        email: String,
        password: String,
    ): Patient

    suspend fun getSession(): PatientSession?

    suspend fun clearSession()
}
