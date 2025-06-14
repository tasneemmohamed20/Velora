package com.example.m_commerce.domain.usecases

import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.repository.IDraftOrderRepository
import javax.inject.Inject

class DraftOrderUseCase @Inject constructor(private val repository: IDraftOrderRepository) {

    suspend operator fun invoke(
        lineItems: List<com.example.m_commerce.service1.type.LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ) = repository.createDraftOrder(lineItems, variantId, note, email, quantity)

    suspend operator fun invoke(id: String) = repository.getDraftOrderById(id)
}