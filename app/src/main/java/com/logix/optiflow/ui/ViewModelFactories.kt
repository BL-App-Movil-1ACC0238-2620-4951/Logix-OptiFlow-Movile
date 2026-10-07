package com.logix.optiflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.logix.optiflow.di.SearchBookingModule
import com.logix.optiflow.ui.auth.AuthViewModel
import com.logix.optiflow.ui.booking.BookingViewModel
import com.logix.optiflow.ui.search.StoreSearchViewModel
import java.util.UUID

object ViewModelFactories {

    val storeSearch: ViewModelProvider.Factory =
        simpleFactory {
            StoreSearchViewModel(
                getOpticalStores = SearchBookingModule.getOpticalStoresUseCase,
                searchOpticalStores = SearchBookingModule.searchOpticalStoresUseCase,
            )
        }

    val auth: ViewModelProvider.Factory =
        simpleFactory {
            AuthViewModel(
                registerPatient = SearchBookingModule.registerPatientUseCase,
                loginPatient = SearchBookingModule.loginPatientUseCase,
                getPatientSession = SearchBookingModule.getPatientSessionUseCase,
                clearSession = { SearchBookingModule.patientRepository.clearSession() },
            )
        }

    fun booking(storeId: UUID): ViewModelProvider.Factory =
        simpleFactory {
            BookingViewModel(
                storeId = storeId,
                getOpticalStores = SearchBookingModule.getOpticalStoresUseCase,
                getAvailability = SearchBookingModule.getStoreAvailabilityUseCase,
                bookAppointment = SearchBookingModule.bookAppointmentUseCase,
                getPatientSession = SearchBookingModule.getPatientSessionUseCase,
            )
        }

    private inline fun <reified T : ViewModel> simpleFactory(
        crossinline create: () -> T,
    ): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
        }
}
