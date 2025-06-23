
package com.example.m_commerce.data.datasource.remote.graphql.favorite

import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import kotlinx.coroutines.flow.Flow

interface IFavoriteRemoteDataSource {
    suspend fun addProductToFavorites(email: String, variantID: String, quantity: Int): DraftOrder
    suspend fun getFavoriteDraftOrders(query: String): Flow<List<DraftOrder>>
    suspend fun updateDraftOrder(draftOrderId: String, lineItems: List<Item>): DraftOrder
}
