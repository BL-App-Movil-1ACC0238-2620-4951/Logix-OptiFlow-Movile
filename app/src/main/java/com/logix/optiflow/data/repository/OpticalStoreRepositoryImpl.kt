package com.logix.optiflow.data.repository

import com.logix.optiflow.data.mapper.toDomain
import com.logix.optiflow.data.remote.api.SearchBookingApi
import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.domain.model.OpticalStoreSearchResult
import com.logix.optiflow.domain.repository.OpticalStoreRepository
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpticalStoreRepositoryImpl(
    private val api: SearchBookingApi,
) : OpticalStoreRepository {

    override suspend fun getOpticalStores(
        name: String?,
        address: String?,
    ): List<OpticalStore> =
        withContext(Dispatchers.IO) {
            api
                .listOpticalStores(name = name, address = address)
                .opticalStores
                .orEmpty()
                .map { it.toDomain() }
        }

    override suspend fun searchOpticalStores(
        name: String?,
        address: String?,
        minRating: Double?,
    ): OpticalStoreSearchResult =
        withContext(Dispatchers.IO) {
            val response =
                api.searchOpticalStores(
                    name = name,
                    address = address,
                    minRating = minRating?.let { BigDecimal.valueOf(it) },
                )
            OpticalStoreSearchResult(
                stores = response.opticalStores.orEmpty().map { it.toDomain() },
                message = response.message,
            )
        }
}
