package com.example.m_commerce.data.datasource.remote.graphql.draft_order

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.data.datasource.remote.graphql.draft_orders.IDraftOrderRemoteDataSource
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.DraftOrderLineItemConnection
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDraftOrderRemoteDataSource(
    val draftOrder: List<DraftOrder>?
) : IDraftOrderRemoteDataSource {

    override suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder {
        return DraftOrder(
            id = "fake_draft_order_id",
            lineItems = DraftOrderLineItemConnection(nodes = lineItems),
            email = email,
            note2 = note.getOrNull(),
            totalPrice = 100.0
        )
    }

    override suspend fun getDraftOrderById(id: String): Flow<List<DraftOrder>>? {
        return draftOrder?.let { flowOf(it) }
    }

    override suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>
    ): DraftOrder {
        val convertedLineItems = lineItems.map { item ->
            LineItem(
                id = "line_item_${item.variantID}",
                variantId = item.variantID,
                quantity = item.quantity,
                title = "Product Title",
                name = "Product Name"
            )
        }
        return DraftOrder(
            id = id,
            lineItems = DraftOrderLineItemConnection(nodes = convertedLineItems),
            email = "test@example.com",
            note2 = "cart",
            totalPrice = 150.0
        )
    }

    override suspend fun updateDraftOrderBillingAddress(
        id: String,
        billingAddress: BillingAddress
    ): DraftOrder {
        return DraftOrder(
            id = id,
            lineItems = DraftOrderLineItemConnection(nodes = emptyList()),
            email = "test@example.com",
            note2 = "cart",
            totalPrice = 100.0,
            billingAddress = billingAddress
        )
    }

    override suspend fun deleteDraftOrder(id: String): Boolean {
        return true
    }

    override suspend fun completeAndDeleteDraftOrder(draftOrderId: String): Boolean {
        return true
    }

    override suspend fun updateDraftOrderApplyVoucher(
        id: String,
        discountCode: List<String>
    ): DraftOrder {
        return DraftOrder(
            id = id,
            lineItems = DraftOrderLineItemConnection(nodes = emptyList()),
            email = "test@example.com",
            note2 = "cart",
            totalPrice = 80.0,
            discountCodes = discountCode
        )
    }
}
