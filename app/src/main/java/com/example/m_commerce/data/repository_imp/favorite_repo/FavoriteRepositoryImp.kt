package com.example.m_commerce.data.repository_imp.favorite_repo

import com.example.m_commerce.data.datasource.remote.graphql.favorite.IFavoriteRemoteDataSource
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.repository.IFavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImp @Inject constructor(
    private val favoriteRemoteDataSource: IFavoriteRemoteDataSource
) : IFavoriteRepository {

    override suspend fun addProductToFavorites(email: String, variantId: String, quantity: Int): DraftOrder =
        favoriteRemoteDataSource.addProductToFavorites(email, variantId, quantity)

    override suspend fun getFavoriteDraftOrders(query: String): Flow<List<DraftOrder>> =
        favoriteRemoteDataSource.getFavoriteDraftOrders(query)

    override suspend fun updateDraftOrder(draftOrderId: String, lineItems: List<Item>): DraftOrder =
        favoriteRemoteDataSource.updateDraftOrder(draftOrderId, lineItems)

    override suspend fun deleteDraftOrder(draftOrderId: String) =
        favoriteRemoteDataSource.deleteDraftOrder(draftOrderId)
}
