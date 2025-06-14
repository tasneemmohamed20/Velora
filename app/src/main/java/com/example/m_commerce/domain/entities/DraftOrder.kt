package com.example.m_commerce.domain.entities

data class DraftOrder(
    val acceptAutomaticDiscounts: Boolean? = null,
    val allowDiscountCodesInCheckout: Boolean? = null,
    val billingAddressMatchesShippingAddress: Boolean? = null,
    val completedAt: String? = null,
    val createdAt: String? = null,
    val currencyCode: String? = null,
    val defaultCursor: String? = null,
    val discountCodes: List<String>? = null,
    val email: String? = null,
    val hasTimelineComment: Boolean? = null,
    val id: String? = null,
    val invoiceEmailTemplateSubject: String? = null,
    val invoiceSentAt: String? = null,
    val invoiceUrl: String? = null,
    val legacyResourceId: String? = null,
    val marketName: String? = null,
    val marketRegionCountryCode: String? = null,
    val name: String? = null,
    val note2: String? = null,
    val phone: String? = null,
    val poNumber: String? = null,
    val presentmentCurrencyCode: String? = null,
    val ready: Boolean? = null,
    val reserveInventoryUntil: String? = null,
    val status: String? = null,
    val subtotalPrice: Double? = null,
    val tags: List<String>? = null,
    val taxExempt: Boolean? = null,
    val taxesIncluded: Boolean? = null,
    val totalPrice: Double? = null,
    val totalQuantityOfLineItems: Int? = null,
    val totalShippingPrice: Double? = null,
    val totalTax: Double? = null,
    val totalWeight: Double? = null,
    val transformerFingerprint: String? = null,
    val updatedAt: String? = null,
    val visibleToCustomer: Boolean? = null,
    val userErrors: List<UserError>? = null,
    val lineItems: DraftOrderLineItemConnection? = null

)

data class DraftOrderLineItemConnection(
    val nodes: List<LineItem>? = null
)

data class LineItem(
    val custom: Boolean? = null,
    val discountedTotal: Double? = null,
    val discountedUnitPrice: Double? = null,
    val grams: Int? = null,
    val id: String? = null,
    val isGiftCard: Boolean? = null,
    val name: String? = null,
    val originalTotal: Double? = null,
    val originalUnitPrice: Double? = null,
    val quantity: Int? = null,
    val requiresShipping: Boolean? = null,
    val sku: String? = null,
    val taxable: Boolean? = null,
    val title: String? = null,
    val totalDiscount: Double? = null,
    val uuid: String? = null,
    val variantTitle: String? = null,
    val vendor: String? = null,
    val image: Image? = null,
    val product: Product? = null
)

data class UserError(
    val field: String? = null,
    val message: String? = null
)

data class Image(
    val url: String? = null
)