package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.Patient
import com.logix.optiflow.domain.repository.PatientRepository

class LoginPatientUseCase(
    private val repository: PatientRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Patient = repository.login(email, password)
}
