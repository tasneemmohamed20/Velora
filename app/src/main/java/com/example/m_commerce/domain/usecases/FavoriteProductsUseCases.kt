package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.repository.IFavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteProductsUseCases @Inject constructor(
    private val repository: IFavoriteRepository
) {
    suspend fun addToFavorites(
        email: String,
        variantId: String,
        quantity: Int
    ): DraftOrder = repository.addProductToFavorites(email, variantId, quantity)


    suspend fun getFavoriteDraftOrders(query: String): Flow<List<DraftOrder>> =
        repository.getFavoriteDraftOrders(query)


    suspend fun updateDraftOrder(
        draftOrderId: String,
        lineItems: List<Item>
    ): DraftOrder = repository.updateDraftOrder(draftOrderId, lineItems)
}