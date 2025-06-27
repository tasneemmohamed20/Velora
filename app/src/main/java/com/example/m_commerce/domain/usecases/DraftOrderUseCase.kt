package com.example.m_commerce.domain.usecases

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.repository.IDraftOrderRepository
import javax.inject.Inject
import com.example.m_commerce.domain.entities.LineItem


class DraftOrderUseCase @Inject constructor(private val repository: IDraftOrderRepository) {

    suspend operator fun invoke(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ) = repository.createDraftOrder(lineItems, variantId, note, email, quantity)

    suspend operator fun invoke(id: String) = repository.getDraftOrderById(id)

    suspend operator fun invoke(
        id: String,
        lineItems: List<Item>
    ) = repository.updateDraftOrder(id, lineItems)

    suspend operator fun invoke(
        id: String,
        billingAddress: BillingAddress
    ) = repository.updateDraftOrderBillingAddress(id, billingAddress)

    suspend fun deleteDraftOrder(id: String) = repository.deleteDraftOrder(id)

    suspend fun updateDraftOrderApplyVoucher(id: String, voucherCode: List<String>) =
        repository.updateDraftOrderApplyVoucher(id, voucherCode)
}
