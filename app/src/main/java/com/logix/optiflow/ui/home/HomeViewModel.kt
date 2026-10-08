package com.logix.optiflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logix.optiflow.domain.usecase.GetOpticalStoresUseCase
import com.logix.optiflow.domain.usecase.GetPatientAppointmentsUseCase
import com.logix.optiflow.domain.usecase.GetPatientSessionUseCase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeAppointmentUi(
    val title: String,
    val storeName: String,
    val dayOfMonth: String,
    val monthLabel: String,
    val timeLabel: String,
    val statusLabel: String,
    val isDemo: Boolean,
)

data class FeaturedFrameUi(
    val imageRes: Int,
    val category: String,
    val name: String,
    val price: String,
)

data class HomeUiState(
    val greetingName: String = "Paciente",
    val nextAppointment: HomeAppointmentUi? = null,
    val activePrescriptions: Int = 2,
    val ordersInTransit: Int = 1,
    val featuredFrames: List<FeaturedFrameUi> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    private val getPatientSession: GetPatientSessionUseCase,
    private val getPatientAppointments: GetPatientAppointmentsUseCase,
    private val getOpticalStores: GetOpticalStoresUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val monthFormatter =
        DateTimeFormatter.ofPattern("MMM", Locale("es", "PE")).withZone(ZoneId.systemDefault())
    private val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale("es", "PE")).withZone(ZoneId.systemDefault())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = getPatientSession()
            val firstName =
                session
                    ?.name
                    ?.trim()
                    ?.substringBefore(' ')
                    ?.takeIf { it.isNotEmpty() }
                    ?: "Paciente"

            val storeNames =
                runCatching { getOpticalStores() }
                    .getOrDefault(emptyList())
                    .associate { it.id to it.name }

            val appointmentUi =
                session?.let { s ->
                    runCatching { getPatientAppointments(s.patientId) }
                        .getOrDefault(emptyList())
                        .minByOrNull { it.startDateTime }
                        ?.let { appt ->
                            toAppointmentUi(
                                title = "Evaluación visual integral",
                                storeName = storeNames[appt.opticalStoreId] ?: "Óptica OptiFlow",
                                start = appt.startDateTime,
                                status = appt.status,
                                isDemo = false,
                            )
                        }
                }

            _uiState.update {
                it.copy(
                    greetingName = firstName,
                    nextAppointment = appointmentUi ?: demoAppointment(),
                    featuredFrames = defaultCatalog(),
                    isLoading = false,
                )
            }
        }
    }

    private fun toAppointmentUi(
        title: String,
        storeName: String,
        start: Instant,
        status: String,
        isDemo: Boolean,
    ): HomeAppointmentUi {
        val day = DateTimeFormatter.ofPattern("d").withZone(ZoneId.systemDefault()).format(start)
        val month = monthFormatter.format(start).uppercase(Locale("es", "PE"))
        val time = timeFormatter.format(start)
        val statusLabel =
            when (status.uppercase(Locale.US)) {
                "CONFIRMED", "CONFIRMADA" -> "Confirmada"
                else -> status.replaceFirstChar { c -> c.titlecase(Locale("es", "PE")) }
            }
        return HomeAppointmentUi(
            title = title,
            storeName = storeName,
            dayOfMonth = day,
            monthLabel = month,
            timeLabel = "$time hrs",
            statusLabel = statusLabel,
            isDemo = isDemo,
        )
    }

    private fun demoAppointment(): HomeAppointmentUi =
        HomeAppointmentUi(
            title = "Evaluación visual integral",
            storeName = "Óptica Visión Norte",
            dayOfMonth = "24",
            monthLabel = "JUN",
            timeLabel = "10:30 hrs",
            statusLabel = "Confirmada",
            isDemo = true,
        )

    private fun defaultCatalog(): List<FeaturedFrameUi> =
        listOf(
            FeaturedFrameUi(
                imageRes = com.logix.optiflow.R.drawable.catalog_verona,
                category = "OFTÁLMICO",
                name = "Verona Classic",
                price = "S/ 120.00",
            ),
            FeaturedFrameUi(
                imageRes = com.logix.optiflow.R.drawable.catalog_havana,
                category = "SOLAR",
                name = "Havana Sun",
                price = "S/ 145.00",
            ),
        )
}
