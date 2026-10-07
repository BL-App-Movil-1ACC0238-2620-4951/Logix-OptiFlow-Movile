package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.Appointment
import com.logix.optiflow.domain.repository.AppointmentRepository
import java.util.UUID

class BookAppointmentUseCase(
    private val repository: AppointmentRepository,
) {
    suspend operator fun invoke(
        opticalStoreId: UUID,
        timeSlotId: UUID,
    ): Appointment = repository.bookAppointment(opticalStoreId, timeSlotId)
}
