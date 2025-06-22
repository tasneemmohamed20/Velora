package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import kotlinx.coroutines.flow.Flow
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.OrderCreateResponse

interface IDraftOrderRepository {
    suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: com.apollographql.apollo.api.Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder

    suspend fun getDraftOrderById(id: String): Flow<List<DraftOrder>>?

    suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>
    ): DraftOrder

    suspend fun updateDraftOrderBillingAddress(
        id: String,
        billingAddress: BillingAddress
    ): DraftOrder

    suspend fun deleteDraftOrder(id: String): Boolean

    suspend fun completeDraftOrder(draftOrderId: String): Boolean
}