package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import kotlinx.coroutines.flow.Flow
import com.example.m_commerce.domain.entities.LineItem

interface IDraftOrderRepository {
    suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: com.apollographql.apollo.api.Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder

    suspend fun getDraftOrderById(id: String): Flow<DraftOrder>?

    suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>,
    ): DraftOrder

    suspend fun deleteDraftOrder(id: String): Boolean
}