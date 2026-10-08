package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.NotificationItem
import com.logix.optiflow.domain.repository.NotificationRepository
import java.util.UUID

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(patientId: UUID): List<NotificationItem> =
        repository.loadForPatient(patientId)
}
