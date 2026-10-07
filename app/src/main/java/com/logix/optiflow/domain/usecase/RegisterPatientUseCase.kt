package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.Patient
import com.logix.optiflow.domain.repository.PatientRepository

class RegisterPatientUseCase(
    private val repository: PatientRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String,
    ): Patient = repository.register(name, email, phone, password)
}
