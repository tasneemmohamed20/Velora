package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.DiscountCodes
import kotlinx.coroutines.flow.Flow

interface IDiscountCodesRepository {
    suspend fun getDiscountCodes() : Flow<List<DiscountCodes>>

}