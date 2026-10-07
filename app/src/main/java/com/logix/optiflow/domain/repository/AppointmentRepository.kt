package com.logix.optiflow.domain.repository

import com.logix.optiflow.domain.model.Appointment
import com.logix.optiflow.domain.model.TimeSlot
import java.util.UUID

interface AppointmentRepository {
    suspend fun getAvailability(opticalStoreId: UUID): List<TimeSlot>

    suspend fun bookAppointment(
        opticalStoreId: UUID,
        timeSlotId: UUID,
    ): Appointment
}
