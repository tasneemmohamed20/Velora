package com.example.m_commerce.data.datasource.remote.graphql.draft_orders

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.OrderCreateResponse
import com.example.m_commerce.service1.UpdateDraftOrderBillingAddressMutation
import kotlinx.coroutines.flow.Flow

interface IDraftOrderRemoteDataSource {
    suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
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

    suspend fun completeAndDeleteDraftOrder(draftOrderId: String): Boolean
}