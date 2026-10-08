package com.logix.optiflow.domain.repository

import com.logix.optiflow.domain.model.NotificationItem
import java.util.UUID

interface NotificationRepository {
    suspend fun loadForPatient(patientId: UUID): List<NotificationItem>

    suspend fun getReadIds(): Set<String>

    suspend fun markAllRead(ids: Collection<String>)

    suspend fun markRead(id: String)
}
