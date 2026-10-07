package com.logix.optiflow.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logix.optiflow.data.remote.toUserMessage
import com.logix.optiflow.domain.model.Appointment
import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.domain.model.PatientSession
import com.logix.optiflow.domain.model.TimeSlot
import com.logix.optiflow.domain.usecase.BookAppointmentUseCase
import com.logix.optiflow.domain.usecase.GetOpticalStoresUseCase
import com.logix.optiflow.domain.usecase.GetPatientSessionUseCase
import com.logix.optiflow.domain.usecase.GetStoreAvailabilityUseCase
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingUiState(
    val store: OpticalStore? = null,
    val session: PatientSession? = null,
    val slots: List<TimeSlot> = emptyList(),
    val selectedSlotId: UUID? = null,
    val confirmedAppointment: Appointment? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class BookingViewModel(
    private val storeId: UUID,
    private val getOpticalStores: GetOpticalStoresUseCase,
    private val getAvailability: GetStoreAvailabilityUseCase,
    private val bookAppointment: BookAppointmentUseCase,
    private val getPatientSession: GetPatientSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val store = getOpticalStores().firstOrNull { it.id == storeId }
                val session = getPatientSession()
                _uiState.update { it.copy(store = store, session = session, isLoading = false) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    fun refreshSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(session = getPatientSession()) }
        }
    }

    fun loadSlots() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val slots = getAvailability(storeId)
                _uiState.update { it.copy(slots = slots, isLoading = false) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }

    fun selectSlot(slotId: UUID) {
        _uiState.update { it.copy(selectedSlotId = slotId, errorMessage = null) }
    }

    fun confirmBooking() {
        val slotId = _uiState.value.selectedSlotId
        if (slotId == null) {
            _uiState.update { it.copy(errorMessage = "Selecciona un horario.") }
            return
        }
        if (_uiState.value.session == null) {
            _uiState.update { it.copy(errorMessage = "Debes iniciar sesión antes de reservar.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val appointment = bookAppointment(storeId, slotId)
                _uiState.update {
                    it.copy(isLoading = false, confirmedAppointment = appointment)
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }
}
