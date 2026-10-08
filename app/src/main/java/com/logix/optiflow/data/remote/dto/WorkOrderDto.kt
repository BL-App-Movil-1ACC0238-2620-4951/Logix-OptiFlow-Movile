package com.logix.optiflow.data.remote.dto

import java.time.Instant
import java.util.UUID

data class WorkOrderListResponse(
    val value: List<WorkOrderDto>?,
)

data class WorkOrderDto(
    val id: UUID,
    val patientId: UUID?,
    val status: String,
    val updatedAt: Instant?,
    val createdAt: Instant?,
)
