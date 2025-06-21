package com.example.m_commerce.data.repository_imp.draft_order

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.data.datasource.remote.graphql.draft_orders.IDraftOrderRemoteDataSource
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.repository.IDraftOrderRepository
import com.example.m_commerce.domain.entities.LineItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DraftOrderRepositoryImp @Inject constructor(private val remoteDataSource: IDraftOrderRemoteDataSource) :IDraftOrderRepository {
    override suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder {
        return remoteDataSource.createDraftOrder(lineItems, variantId, note, email, quantity)
    }

    override suspend fun getDraftOrderById(id: String): Flow<DraftOrder>? {
        return remoteDataSource.getDraftOrderById(id)
    }

    override suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>
    ): DraftOrder {
        return remoteDataSource.updateDraftOrder(id, lineItems)
    }

    override suspend fun deleteDraftOrder(id: String): Boolean {
        return remoteDataSource.deleteDraftOrder(id)
    }
}