package com.logix.optiflow.domain.repository

import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.domain.model.OpticalStoreSearchResult

interface OpticalStoreRepository {
    suspend fun getOpticalStores(
        name: String? = null,
        address: String? = null,
    ): List<OpticalStore>

    suspend fun searchOpticalStores(
        name: String? = null,
        address: String? = null,
        minRating: Double? = null,
    ): OpticalStoreSearchResult
}
