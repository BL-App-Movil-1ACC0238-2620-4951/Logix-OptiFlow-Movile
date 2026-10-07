package com.logix.optiflow.domain.usecase

import com.logix.optiflow.domain.model.OpticalStoreSearchResult
import com.logix.optiflow.domain.repository.OpticalStoreRepository

class SearchOpticalStoresUseCase(
    private val repository: OpticalStoreRepository,
) {
    suspend operator fun invoke(
        name: String? = null,
        address: String? = null,
        minRating: Double? = null,
    ): OpticalStoreSearchResult = repository.searchOpticalStores(name, address, minRating)
}
