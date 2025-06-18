package com.example.m_commerce.data.datasource.remote.graphql.draft_orders

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import kotlinx.coroutines.flow.Flow

interface IDraftOrderRemoteDataSource {
    suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder

    suspend fun getDraftOrderById(id: String): Flow<DraftOrder>?

    suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>,
    ): DraftOrder
}