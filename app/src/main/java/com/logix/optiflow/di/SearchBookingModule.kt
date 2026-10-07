package com.logix.optiflow.di

import android.content.Context
import com.logix.optiflow.data.local.PatientSessionStore
import com.logix.optiflow.data.remote.ApiClient
import com.logix.optiflow.data.repository.AppointmentRepositoryImpl
import com.logix.optiflow.data.repository.OpticalStoreRepositoryImpl
import com.logix.optiflow.data.repository.PatientRepositoryImpl
import com.logix.optiflow.domain.repository.AppointmentRepository
import com.logix.optiflow.domain.repository.OpticalStoreRepository
import com.logix.optiflow.domain.repository.PatientRepository
import com.logix.optiflow.domain.usecase.BookAppointmentUseCase
import com.logix.optiflow.domain.usecase.GetOpticalStoresUseCase
import com.logix.optiflow.domain.usecase.GetPatientSessionUseCase
import com.logix.optiflow.domain.usecase.GetStoreAvailabilityUseCase
import com.logix.optiflow.domain.usecase.LoginPatientUseCase
import com.logix.optiflow.domain.usecase.RegisterPatientUseCase

object SearchBookingModule {

    private lateinit var appContext: Context

    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    private fun requireContext(): Context {
        check(::appContext.isInitialized) {
            "SearchBookingModule.init(context) must be called first"
        }
        return appContext
    }

    val opticalStoreRepository: OpticalStoreRepository by lazy {
        OpticalStoreRepositoryImpl(api = ApiClient.searchBookingApi)
    }

    val getOpticalStoresUseCase: GetOpticalStoresUseCase by lazy {
        GetOpticalStoresUseCase(opticalStoreRepository)
    }

    private val patientSessionStore: PatientSessionStore by lazy {
        PatientSessionStore(requireContext())
    }

    val patientRepository: PatientRepository by lazy {
        PatientRepositoryImpl(
            api = ApiClient.searchBookingApi,
            sessionStore = patientSessionStore,
        )
    }

    val registerPatientUseCase: RegisterPatientUseCase by lazy {
        RegisterPatientUseCase(patientRepository)
    }

    val loginPatientUseCase: LoginPatientUseCase by lazy {
        LoginPatientUseCase(patientRepository)
    }

    val getPatientSessionUseCase: GetPatientSessionUseCase by lazy {
        GetPatientSessionUseCase(patientRepository)
    }

    val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl(
            api = ApiClient.searchBookingApi,
            sessionStore = patientSessionStore,
        )
    }

    val getStoreAvailabilityUseCase: GetStoreAvailabilityUseCase by lazy {
        GetStoreAvailabilityUseCase(appointmentRepository)
    }

    val bookAppointmentUseCase: BookAppointmentUseCase by lazy {
        BookAppointmentUseCase(appointmentRepository)
    }
}
