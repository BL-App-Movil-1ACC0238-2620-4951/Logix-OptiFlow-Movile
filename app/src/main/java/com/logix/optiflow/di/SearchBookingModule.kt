package com.logix.optiflow.di

import com.logix.optiflow.data.remote.ApiClient
import com.logix.optiflow.data.repository.OpticalStoreRepositoryImpl
import com.logix.optiflow.domain.repository.OpticalStoreRepository
import com.logix.optiflow.domain.usecase.GetOpticalStoresUseCase

object SearchBookingModule {

    val opticalStoreRepository: OpticalStoreRepository by lazy {
        OpticalStoreRepositoryImpl(api = ApiClient.searchBookingApi)
    }

    val getOpticalStoresUseCase: GetOpticalStoresUseCase by lazy {
        GetOpticalStoresUseCase(opticalStoreRepository)
    }
}
