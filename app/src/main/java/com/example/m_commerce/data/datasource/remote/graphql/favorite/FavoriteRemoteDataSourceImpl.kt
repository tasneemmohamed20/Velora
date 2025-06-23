package com.example.m_commerce.data.datasource.remote.graphql.favorite

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.*
import com.example.m_commerce.service1.DraftOrderCreateMutation
import com.example.m_commerce.service1.DraftOrderUpdateMutation
import com.example.m_commerce.service1.GetDraftOrdersQuery
import com.example.m_commerce.service1.type.DraftOrderLineItemInput
import com.example.m_commerce.service1.DraftOrderDeleteMutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteRemoteDataSourceImpl @Inject constructor(
    @AdminApollo private val apolloClient: ApolloClient
) : IFavoriteRemoteDataSource {

    override suspend fun addProductToFavorites(
        email: String,
        variantID: String,
        quantity: Int
    ): DraftOrder = withContext(Dispatchers.IO) {
        try {
            val response = apolloClient.mutation(
                DraftOrderCreateMutation(
                    email = email,
                    note = Optional.present(note.fav.name),
                    quantity = quantity,
                    variantId = variantID
                )
            ).execute()

            val draft = response.data?.draftOrderCreate?.draftOrder
                ?: throw Exception("Failed to create favorite draft order")

            DraftOrder(
                id = draft.id,
                email = draft.email,
                name = draft.name,
                status = draft.status?.toString(),
                totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                updatedAt = draft.updatedAt?.toString(),
                lineItems = draft.lineItems?.let { lineItems ->
                    DraftOrderLineItemConnection(
                        nodes = lineItems.nodes?.map { node ->
                            LineItem(
                                id = node.id,
                                quantity = node.quantity,
                                variantId = node.variant?.id
                            )
                        }
                    )
                }
            )
        } catch (e: Exception) {
            throw Exception("Failed to add product to favorites: ${e.message}")
        }
    }

    override suspend fun getFavoriteDraftOrders(query: String): Flow<List<DraftOrder>> = flow {
        try {
            val response = apolloClient.query(
                GetDraftOrdersQuery(Optional.present(query))
            ).execute()

            var draftOrders = response.data?.draftOrders?.nodes?.mapNotNull { draft ->
                    DraftOrder(
                        id = draft.id,
                        email = draft.email,
                        name = draft.name,
                        note2 = draft.note2,
                        totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                        lineItems = draft.lineItems?.let { lineItems ->
                            DraftOrderLineItemConnection(
                                nodes = lineItems.nodes?.map { node ->
                                    LineItem(
                                        id = node.id,
                                        name = node.name,
                                        originalTotal = node.originalTotal?.toString()?.toDoubleOrNull(),
                                        originalUnitPrice = node.originalUnitPrice?.toString()?.toDoubleOrNull(),
                                        title = node.title,
                                        variantTitle = node.variantTitle,
                                        vendor = node.vendor,
                                        image = node.image?.let { image ->
                                            Image(
                                                url = image.url?.toString()
                                            )
                                        },
                                        product = node.product?.let { product ->
                                            Product(
                                                id = "",
                                                title = "",
                                                productType = "",
                                                description = "",
                                                price = PriceDetails(
                                                    minVariantPrice = Price(
                                                        amount = product.priceRange.minVariantPrice.amount.toString(),
                                                        currencyCode = ""
                                                    )
                                                ),
                                                images =  emptyList(),
                                                variants = product.variants.edges.map { edge ->
                                                    ProductVariant(
                                                        id = edge.node.id,
                                                        title = edge.node.title,
                                                        availableForSale = edge.node.availableForSale == true,
                                                        selectedOptions = edge.node.selectedOptions?.map { option ->
                                                            SelectedOption(
                                                                name = option.name,
                                                                value = option.value
                                                            )
                                                        }
                                                    )
                                                }
                                            )
                                        },
                                        variantId = node.variant?.id

                                    )
                                }
                            )
                        }
                    )
            } ?: emptyList()
            emit(draftOrders)
        } catch (e: Exception) {
            throw Exception("Failed to fetch favorite draft orders: ${e.message}")
        }
    }

    private fun sanitizeVariantId(variantId: String?): String {
        if (variantId == null) return ""
        return when {
            variantId.contains("gid://shopify/ProductVariant/gid://shopify/DraftOrderLineItem/") -> {
                val id = variantId.substringAfterLast("/DraftOrderLineItem/")
                "gid://shopify/ProductVariant/$id"
            }
            variantId.contains("Present(value=") -> {
                variantId.substringAfter("Present(value=").substringBefore(")")
            }
            !variantId.startsWith("gid://shopify/ProductVariant/") -> {
                "gid://shopify/ProductVariant/$variantId"
            }
            else -> variantId
        }
    }

    override suspend fun updateDraftOrder(
        draftOrderId: String,
        lineItems: List<Item>
    ): DraftOrder {
        val draftOrderLineItems = lineItems.map { item ->
            val sanitizedVariantId = sanitizeVariantId(item.variantID)
            android.util.Log.d("FavoriteRemoteDataSourceImpl", "LineItem: variantId=$sanitizedVariantId, quantity=${item.quantity}")

            DraftOrderLineItemInput(
                variantId = Optional.present(sanitizedVariantId),
                quantity = item.quantity ?: 1
            )
        }

        val response = withContext(Dispatchers.IO) {
            apolloClient.mutation(
                DraftOrderUpdateMutation(
                    id = draftOrderId,
                    lineItems = draftOrderLineItems
                )
            ).execute()
        }

        android.util.Log.d("FavoriteDraftOrderUpdate", "Request ID: $draftOrderId")
        android.util.Log.d("FavoriteDraftOrderUpdate", "LineItems: $draftOrderLineItems")
        android.util.Log.d("FavoriteDraftOrderUpdate", "Response: ${response.data?.draftOrderUpdate?.userErrors}")

        response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.let { error ->
            android.util.Log.e("FavoriteDraftOrderUpdate", "Error: ${error.message}")
        }

        return response.data?.draftOrderUpdate?.draftOrder?.let { draft ->
            DraftOrder(
                id = draft.id,
                email = draft.email,
                name = draft.name,
                status = draft.status?.toString(),
                totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                updatedAt = draft.updatedAt?.toString(),
                lineItems = draft.lineItems?.let { lineItems ->
                    DraftOrderLineItemConnection(
                        nodes = lineItems.nodes?.map { node ->
                            LineItem(
                                id = node.id,
                                title = node.title,
                                quantity = node.quantity,
                                requiresShipping = node.requiresShipping,
                                taxable = node.taxable
                            )
                        } ?: emptyList()
                    )
                }
            )
        } ?: throw Exception("Failed to update draft order: ${response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.message ?: "No response data"}")
    }

    override suspend fun deleteDraftOrder(draftOrderId: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                apolloClient.mutation(
                   DraftOrderDeleteMutation(id = draftOrderId)
                ).execute()
            }
            val success = response.data?.draftOrderDelete?.userErrors?.isEmpty() ?: false
            if (!success) {
                response.data?.draftOrderDelete?.userErrors?.forEach { error ->
                    android.util.Log.e("DraftOrderDelete", "Error: ${error.message}, Field: ${error.field}")
                }
                throw Exception("Failed to delete draft order")
            }
        } catch (e: Exception) {
            android.util.Log.e("DraftOrderDelete", "Failed to delete draft order", e)
            throw e
        }
    }
}