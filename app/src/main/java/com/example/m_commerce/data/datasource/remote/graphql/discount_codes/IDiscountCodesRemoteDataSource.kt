package com.example.m_commerce.data.datasource.remote.graphql.discount_codes

import com.example.m_commerce.domain.entities.DiscountCodes
import kotlinx.coroutines.flow.Flow

interface IDiscountCodesRemoteDataSource{
    suspend fun getDiscountCodes() : Flow<List<DiscountCodes>>
}