package com.logix.optiflow.data.repository

import com.logix.optiflow.data.local.PatientSessionStore
import com.logix.optiflow.data.mapper.toDomain
import com.logix.optiflow.data.remote.api.SearchBookingApi
import com.logix.optiflow.data.remote.dto.LoginRequest
import com.logix.optiflow.data.remote.dto.RegisterPatientRequest
import com.logix.optiflow.domain.model.Patient
import com.logix.optiflow.domain.model.PatientSession
import com.logix.optiflow.domain.repository.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PatientRepositoryImpl(
    private val api: SearchBookingApi,
    private val sessionStore: PatientSessionStore,
) : PatientRepository {

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
    ): Patient =
        withContext(Dispatchers.IO) {
            val patient =
                api
                    .registerPatient(
                        RegisterPatientRequest(
                            name = name,
                            email = email,
                            phone = phone,
                            password = password,
                        ),
                    )
                    .toDomain()
            sessionStore.save(
                patientId = patient.id,
                name = patient.name,
                email = patient.email,
                token = null,
            )
            patient
        }

    override suspend fun login(
        email: String,
        password: String,
    ): Patient =
        withContext(Dispatchers.IO) {
            val response = api.login(LoginRequest(email = email, password = password))
            val patient = response.patient.toDomain()
            sessionStore.save(
                patientId = patient.id,
                name = patient.name,
                email = patient.email,
                token = response.token,
            )
            patient
        }

    override suspend fun getSession(): PatientSession? = sessionStore.read()

    override suspend fun clearSession() {
        sessionStore.clear()
    }
}
