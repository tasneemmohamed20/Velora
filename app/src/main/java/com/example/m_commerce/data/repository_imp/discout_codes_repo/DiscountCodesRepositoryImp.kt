package com.example.m_commerce.data.repository_imp.discout_codes_repo

import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.IDiscountCodesRemoteDataSource
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.repository.IDiscountCodesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiscountCodesRepositoryImp @Inject constructor(
    private val remoteDataSource: IDiscountCodesRemoteDataSource
) :IDiscountCodesRepository {
    override suspend fun getDiscountCodes(): Flow<List<DiscountCodes>> {
        return remoteDataSource.getDiscountCodes()
    }
}