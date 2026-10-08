package com.logix.optiflow.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logix.optiflow.R
import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.ui.ViewModelFactories
import com.logix.optiflow.ui.components.OptiFlowScaffold
import java.util.UUID

@Composable
fun StoreSearchScreen(
    onOpenAuth: () -> Unit,
    onSelectStore: (UUID) -> Unit,
    viewModel: StoreSearchViewModel = viewModel(factory = ViewModelFactories.storeSearch),
) {
    val uiState by viewModel.uiState.collectAsState()

    OptiFlowScaffold(title = stringResource(R.string.flow_search_title)) {
        Column(
            modifier = it.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.flow_search_subtitle),
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.nameQuery,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.flow_search_name)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.addressQuery,
                onValueChange = viewModel::onAddressChange,
                label = { Text(stringResource(R.string.flow_search_address)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.minRatingQuery,
                onValueChange = viewModel::onMinRatingChange,
                label = { Text(stringResource(R.string.flow_search_min_rating)) },
                placeholder = { Text(stringResource(R.string.flow_search_min_rating_hint)) },
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.minRatingQuery == "4.0",
                    onClick = { viewModel.setMinRatingPreset(4.0) },
                    label = { Text(stringResource(R.string.flow_filter_rating_40)) },
                )
                FilterChip(
                    selected = uiState.minRatingQuery == "4.5",
                    onClick = { viewModel.setMinRatingPreset(4.5) },
                    label = { Text(stringResource(R.string.flow_filter_rating_45)) },
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::search,
                enabled = !uiState.isLoading,
            ) {
                Text(stringResource(R.string.flow_search_button))
            }
            TextButton(onClick = onOpenAuth, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.flow_open_auth))
            }
            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            } else if (uiState.stores.isEmpty() && uiState.errorMessage == null) {
                Text(uiState.infoMessage ?: stringResource(R.string.flow_search_empty))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.stores, key = { it.id }) { store ->
                        StoreCard(store = store, onClick = { onSelectStore(store.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreCard(
    store: OpticalStore,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = store.name, style = MaterialTheme.typography.titleMedium)
            Text(text = store.address, style = MaterialTheme.typography.bodyMedium)
            Text(
                text =
                    buildString {
                        store.rating?.let { append("★ ${"%.1f".format(it)}  ·  ") }
                        append(store.phone)
                    },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
