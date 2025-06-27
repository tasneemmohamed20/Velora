package com.example.m_commerce.data.repository_imp.favorite_repo

import com.example.m_commerce.data.datasource.remote.graphql.favorite.IFavoriteRemoteDataSource
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.repository.IFavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteRepositoryImpTest {

    private lateinit var remoteDataSource: IFavoriteRemoteDataSource
    private lateinit var repository: IFavoriteRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk(relaxed = true)
        repository = FavoriteRepositoryImp(remoteDataSource)
    }

    @Test
    fun `addProductToFavorites should call remoteDataSource and return DraftOrder`() = runTest {
        val email = "test@example.com"
        val variantId = "v1"
        val quantity = 1
        val draftOrder = DraftOrder(id = "123", name = "Favorites")

        coEvery {
            remoteDataSource.addProductToFavorites(email, variantId, quantity)
        } returns draftOrder

        val result = repository.addProductToFavorites(email, variantId, quantity)

        assertEquals(draftOrder, result)
        coVerify { remoteDataSource.addProductToFavorites(email, variantId, quantity) }
    }

    @Test
    fun `getFavoriteDraftOrders should return flow of DraftOrder list`() = runTest {
        val query = "test"
        val draftOrders = listOf(DraftOrder(id = "1", name = "Fav"))
        coEvery { remoteDataSource.getFavoriteDraftOrders(query) } returns flowOf(draftOrders)

        val flow = repository.getFavoriteDraftOrders(query)
        val result = flow.toList()

        assertEquals(1, result.size)
        assertEquals(draftOrders, result[0])
        coVerify { remoteDataSource.getFavoriteDraftOrders(query) }
    }

    @Test
    fun `updateDraftOrder should call remoteDataSource and return updated DraftOrder`() = runTest {
        val draftOrderId = "123"
        val items = listOf(Item(variantID = "v1", quantity = 1))
        val updatedOrder = DraftOrder(id = draftOrderId, name = "Updated")

        coEvery { remoteDataSource.updateDraftOrder(draftOrderId, items) } returns updatedOrder

        val result = repository.updateDraftOrder(draftOrderId, items)

        assertEquals(updatedOrder, result)
        coVerify { remoteDataSource.updateDraftOrder(draftOrderId, items) }
    }

    @Test
    fun `deleteDraftOrder should call remoteDataSource`() = runTest {
        val draftOrderId = "456"

        coEvery { remoteDataSource.deleteDraftOrder(draftOrderId) } returns Unit

        repository.deleteDraftOrder(draftOrderId)

        coVerify { remoteDataSource.deleteDraftOrder(draftOrderId) }
    }
}
