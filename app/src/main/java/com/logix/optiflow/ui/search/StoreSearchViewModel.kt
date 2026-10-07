package com.logix.optiflow.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logix.optiflow.data.remote.toUserMessage
import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.domain.usecase.GetOpticalStoresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoreSearchUiState(
    val nameQuery: String = "",
    val addressQuery: String = "",
    val stores: List<OpticalStore> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class StoreSearchViewModel(
    private val getOpticalStores: GetOpticalStoresUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreSearchUiState())
    val uiState: StateFlow<StoreSearchUiState> = _uiState.asStateFlow()

    init {
        search()
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(nameQuery = value, errorMessage = null) }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(addressQuery = value, errorMessage = null) }
    }

    fun search() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val name = _uiState.value.nameQuery.trim().ifEmpty { null }
                val address = _uiState.value.addressQuery.trim().ifEmpty { null }
                val stores = getOpticalStores(name = name, address = address)
                _uiState.update { it.copy(stores = stores, isLoading = false) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
            }
        }
    }
}
