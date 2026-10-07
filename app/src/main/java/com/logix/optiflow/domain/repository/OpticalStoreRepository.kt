package com.logix.optiflow.domain.repository

import com.logix.optiflow.domain.model.OpticalStore

interface OpticalStoreRepository {
    suspend fun getOpticalStores(
        name: String? = null,
        address: String? = null,
    ): List<OpticalStore>
}
