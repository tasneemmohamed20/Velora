package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.DraftOrder
import kotlinx.coroutines.flow.Flow

interface IDraftOrderRepository {
    suspend fun createDraftOrder(
        lineItems: List<com.example.m_commerce.service1.type.LineItem>,
        variantId: String,
        note: com.apollographql.apollo.api.Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder

    suspend fun getDraftOrderById(id: String): Flow<DraftOrder>?
}