package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import kotlinx.coroutines.flow.Flow

interface IFavoriteRepository {

    suspend fun addProductToFavorites(email: String, variantId: String, quantity: Int): DraftOrder

    suspend fun getFavoriteDraftOrders(email: String): Flow<List<DraftOrder>>

    suspend fun updateDraftOrder(draftOrderId: String, lineItems: List<Item>): DraftOrder

    suspend fun deleteDraftOrder(draftOrderId: String)
}
