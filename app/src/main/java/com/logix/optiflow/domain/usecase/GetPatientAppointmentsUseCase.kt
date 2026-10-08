package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.Appointment
import com.logix.optiflow.domain.repository.AppointmentRepository
import java.util.UUID

class GetPatientAppointmentsUseCase(
    private val repository: AppointmentRepository,
) {
    suspend operator fun invoke(patientId: UUID): List<Appointment> =
        repository.getPatientAppointments(patientId)
}
