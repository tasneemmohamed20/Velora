package com.example.m_commerce.data.restful.repository_imp

import com.example.m_commerce.data.restful.data_source.remote.RemoteDataSourceContract
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import kotlinx.coroutines.flow.Flow

class GeoCodingRepositoryImp(
    private val remoteDataSource: RemoteDataSourceContract,

    ): IGeoCodingRepository {
    private val geocodingApiKey = "AIzaSyB9cRwZcC2Kirk3Fy2sCEtPUv3zIqRn6Jk"

    override suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String> {
        return remoteDataSource.getAddressFromGeocoding(latlng, geocodingApiKey)
    }
}