package com.logix.optiflow.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.domain.model.TimeSlot
import com.logix.optiflow.ui.components.OptiFlowScaffold
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

private val slotFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())

@Composable
fun BookingScreen(
    storeId: UUID,
    onBack: () -> Unit,
    onNeedAuth: () -> Unit,
    onConfirmed: () -> Unit,
    viewModel: BookingViewModel = viewModel(factory = com.logix.optiflow.ui.ViewModelFactories.booking(storeId)),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshSession()
    }

    uiState.confirmedAppointment?.let { appointment ->
        OptiFlowScaffold(title = stringResource(R.string.flow_confirmation_title), onBack = onBack) {
            Column(
                modifier = it.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(stringResource(R.string.flow_confirmation_body), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.booking_confirmed, appointment.id, appointment.status))
                Button(onClick = onConfirmed, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.flow_back_home))
                }
            }
        }
        return
    }

    OptiFlowScaffold(
        title = uiState.store?.name ?: stringResource(R.string.booking_title),
        onBack = onBack,
    ) {
        Column(
            modifier = it.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.store?.let { store ->
                Text(store.address, style = MaterialTheme.typography.bodyMedium)
            }
            val session = uiState.session
            if (session == null) {
                Text(stringResource(R.string.booking_login_required), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onNeedAuth) {
                    Text(stringResource(R.string.flow_open_auth))
                }
            } else {
                Text(
                    stringResource(R.string.booking_patient_ready, session.email, session.patientId),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::loadSlots,
                enabled = !uiState.isLoading,
            ) {
                Text(stringResource(R.string.booking_load_slots))
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.slots, key = { it.id }) { slot ->
                    SlotRow(
                        slot = slot,
                        selected = uiState.selectedSlotId == slot.id,
                        onSelect = { viewModel.selectSlot(slot.id) },
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::confirmBooking,
                enabled = !uiState.isLoading && uiState.selectedSlotId != null,
            ) {
                Text(stringResource(R.string.booking_confirm))
            }

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun SlotRow(
    slot: TimeSlot,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            RadioButton(selected = selected, onClick = onSelect)
            Text(
                text =
                    stringResource(
                        R.string.booking_slot_item,
                        slotFormatter.format(slot.startDateTime),
                        slotFormatter.format(slot.endDateTime),
                        slot.status,
                    ),
            )
        }
    }
}
