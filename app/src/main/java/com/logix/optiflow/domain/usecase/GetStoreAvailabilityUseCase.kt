package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.TimeSlot
import com.logix.optiflow.domain.repository.AppointmentRepository
import java.util.UUID

class GetStoreAvailabilityUseCase(
    private val repository: AppointmentRepository,
) {
    suspend operator fun invoke(opticalStoreId: UUID): List<TimeSlot> =
        repository.getAvailability(opticalStoreId)
}
