package com.logix.optiflow.data.remote.api

import com.logix.optiflow.data.remote.dto.AppointmentDto
import com.logix.optiflow.data.remote.dto.BookAppointmentRequest
import com.logix.optiflow.data.remote.dto.LoginRequest
import com.logix.optiflow.data.remote.dto.LoginResponse
import com.logix.optiflow.data.remote.dto.OpticalStoreListResponse
import com.logix.optiflow.data.remote.dto.PatientDto
import com.logix.optiflow.data.remote.dto.RegisterPatientRequest
import com.logix.optiflow.data.remote.dto.TimeSlotListResponse
import com.logix.optiflow.data.remote.dto.WorkOrderDto
import com.logix.optiflow.data.remote.dto.ClinicalRecordDto
import java.util.UUID
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchBookingApi {

    @GET("optical-stores")
    suspend fun listOpticalStores(
        @Query("name") name: String? = null,
        @Query("address") address: String? = null,
    ): OpticalStoreListResponse

    @GET("optical-stores/search")
    suspend fun searchOpticalStores(
        @Query("name") name: String? = null,
        @Query("address") address: String? = null,
        @Query("minRating") minRating: Double? = null,
    ): OpticalStoreListResponse

    @GET("optical-stores/{id}/availability")
    suspend fun getAvailability(
        @Path("id") opticalStoreId: UUID,
    ): TimeSlotListResponse

    @POST("patients")
    suspend fun registerPatient(
        @Body request: RegisterPatientRequest,
    ): PatientDto

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    @POST("appointments")
    suspend fun bookAppointment(
        @Body request: BookAppointmentRequest,
    ): AppointmentDto

    @GET("patients/{patientId}/appointments")
    suspend fun getPatientAppointments(
        @Path("patientId") patientId: UUID,
    ): List<AppointmentDto>

    @GET("patients/{patientId}/work-orders")
    suspend fun getPatientWorkOrders(
        @Path("patientId") patientId: UUID,
    ): List<WorkOrderDto>

    @GET("work-orders")
    suspend fun getWorkOrders(
        @Query("status") status: String? = null,
    ): List<WorkOrderDto>

    @GET("patients/{patientId}/clinical-records")
    suspend fun getPatientClinicalRecords(
        @Path("patientId") patientId: UUID,
    ): List<ClinicalRecordDto>
}
