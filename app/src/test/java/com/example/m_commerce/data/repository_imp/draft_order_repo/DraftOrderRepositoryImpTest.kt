package com.example.m_commerce.data.repository_imp.draft_order_repo

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.data.datasource.remote.graphql.draft_order.FakeDraftOrderRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.draft_orders.IDraftOrderRemoteDataSource
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.DraftOrderLineItemConnection
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.repository.IDraftOrderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DraftOrderRepositoryImpTest {
    val remoteDraftOrders: List<DraftOrder> = listOf(
        DraftOrder(
            id = "1",
            lineItems = DraftOrderLineItemConnection(nodes = emptyList()),
            email = "test@example.com",
            note2 = "Test note 1",
            totalPrice = 80.0,
            discountCodes = listOf("code1", "code2", "code3"),
            billingAddress = BillingAddress(
                address1 = "address1",
                address2 = "address2"
            )
        ),
        DraftOrder(
            id = "2",
            lineItems = DraftOrderLineItemConnection(nodes = emptyList()),
            email = "test2@example.com",
            note2 = "Test note 2",
            totalPrice = 90.0,
            discountCodes = listOf("code4", "code5", "code6"),
            billingAddress = BillingAddress(
                address1 = "address3",
                address2 = "address4"
            )
        ),
        DraftOrder(
            id = "3",
            lineItems = DraftOrderLineItemConnection(nodes = emptyList()),
            email = "test3@example.com",
            note2 = "Test note 3",
            totalPrice = 100.0,
            discountCodes = listOf("code7", "code8", "code9"),
            billingAddress = BillingAddress(
                address1 = "address5",
                address2 = "address6"
            )
        )
    )

    private lateinit var fakeDraftOrderDataSource: IDraftOrderRemoteDataSource
    private lateinit var draftOrderRepository: IDraftOrderRepository

    @Before
    fun setUp() {
        fakeDraftOrderDataSource = FakeDraftOrderRemoteDataSource(remoteDraftOrders)
        draftOrderRepository = DraftOrderRepositoryImp(fakeDraftOrderDataSource)
    }

    @Test
    fun `createDraftOrder should return DraftOrder with correct data`() = runTest {
        // Given
        val lineItems = listOf(
            LineItem(
                id = "line_item_1",
                variantId = "variant_1",
                quantity = 2,
                title = "Test Product"
            )
        )
        val variantId = "variant_123"
        val note = Optional.present("cart")
        val email = "test@example.com"
        val quantity = 2

        // When
        val result =
            draftOrderRepository.createDraftOrder(lineItems, variantId, note, email, quantity)

        // Then
        assertEquals("fake_draft_order_id", result.id)
        assertEquals(email, result.email)
        assertEquals("cart", result.note2)
        assertEquals(100.0, result.totalPrice)
        assertEquals(lineItems, result.lineItems?.nodes)
    }

    @Test
    fun `getDraftOrderById should return flow of draft orders`() = runTest {
        // Given
        val id = "1"

        // When
        val result = draftOrderRepository.getDraftOrderById(id)

        // Then
        assertNotNull(result)
        val draftOrders = result!!.first()
        assertEquals(3, draftOrders.size)
        assertEquals("1", draftOrders[0].id)
        assertEquals("2", draftOrders[1].id)
        assertEquals("3", draftOrders[2].id)
    }

    @Test
    fun `updateDraftOrder should return updated DraftOrder`() = runTest {
        // Given
        val id = "test_id"
        val items = listOf(
            Item(variantID = "variant_1", quantity = 3),
            Item(variantID = "variant_2", quantity = 1)
        )

        // When
        val result = draftOrderRepository.updateDraftOrder(id, items)

        // Then
        assertEquals(id, result.id)
        assertEquals("test@example.com", result.email)
        assertEquals("cart", result.note2)
        assertEquals(150.0, result.totalPrice)
        assertEquals(2, result.lineItems?.nodes?.size)
    }

    @Test
    fun `updateDraftOrderBillingAddress should return DraftOrder with updated billing address`() =
        runTest {
            // Given
            val id = "test_id"
            val billingAddress = BillingAddress(
                address1 = "P. Sherman",
                address2 = "42 Wallaby st",
                city = "Sydney",
                phone = "555-1234"
            )

        // When
        val result = draftOrderRepository.updateDraftOrderBillingAddress(id, billingAddress)

        // Then
        assertEquals(id, result.id)
        assertEquals(billingAddress, result.billingAddress)
        assertEquals("cart", result.note2)
        assertEquals(100.0, result.totalPrice)
    }

    @Test
    fun `deleteDraftOrder should return true`() = runTest {
        // Given
        val id = "test_id"

        // When
        val result = draftOrderRepository.deleteDraftOrder(id)

        // Then
        assertTrue(result)
    }

    @Test
    fun `completeDraftOrder should return true`() = runTest {
        // Given
        val draftOrderId = "test_id"

        // When
        val result = draftOrderRepository.completeDraftOrder(draftOrderId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `updateDraftOrderApplyVoucher should return DraftOrder with discount codes`() =
        runTest {
            // Given
            val id = "test_id"
            val discountCodes = listOf("SAVE10", "FREESHIP")

        // When
        val result = draftOrderRepository.updateDraftOrderApplyVoucher(id, discountCodes)

        // Then
        assertEquals(id, result.id)
        assertEquals(discountCodes, result.discountCodes)
        assertEquals("cart", result.note2)
        assertEquals(80.0, result.totalPrice)
    }

    @Test
    fun `createDraftOrder with empty note should handle Optional correctly`() = runTest {
        // Given
        val lineItems = emptyList<LineItem>()
        val variantId = "variant_123"
        val note = Optional.absent()
        val email = "test@example.com"
        val quantity = 1

        // When
        val result =
            draftOrderRepository.createDraftOrder(lineItems, variantId, note, email, quantity)

        // Then
        assertEquals("fake_draft_order_id", result.id)
        assertEquals(email, result.email)
        assertNull(result.note2)
        assertEquals(100.0, result.totalPrice)
    }

    @After
    fun tearDown() {
    }
}
