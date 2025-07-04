package com.example.m_commerce.data.datasource.remote.graphql.draft_orders

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.DraftOrderLineItemConnection
import com.example.m_commerce.domain.entities.Image
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.OrderCreateResponse
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.entities.ProductVariant
import com.example.m_commerce.domain.entities.SelectedOption
import com.example.m_commerce.domain.entities.UserError
import com.example.m_commerce.service1.CompleteDraftOrderMutation
import com.example.m_commerce.service1.DraftOrderCreateMutation
import com.example.m_commerce.service1.DraftOrderDeleteMutation
import com.example.m_commerce.service1.DraftOrderUpdateMutation
import com.example.m_commerce.service1.GetDraftOrdersQuery
import com.example.m_commerce.service1.UpdateDraftOrderApplyDiscountCodeMutation
import com.example.m_commerce.service1.UpdateDraftOrderBillingAddressMutation
import com.example.m_commerce.service1.type.DraftOrderLineItemInput
import com.example.m_commerce.service1.type.MailingAddressInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DraftOrderRemoteDataSourceImp @Inject constructor(@AdminApollo private val shopifyService: ApolloClient) : IDraftOrderRemoteDataSource{

    override suspend fun createDraftOrder(
        lineItems: List<LineItem>,
        variantId: String,
        note: Optional<String?>,
        email: String,
        quantity: Int
    ): DraftOrder {
        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(DraftOrderCreateMutation(
                email = email,
                note = note,
                quantity = quantity,
                variantId = variantId
            )).execute()
        }

        val draftOrder = response.data?.draftOrderCreate?.draftOrder?.let { draft ->
            DraftOrder(
                acceptAutomaticDiscounts = draft.acceptAutomaticDiscounts,
                allowDiscountCodesInCheckout = draft.allowDiscountCodesInCheckout,
                billingAddressMatchesShippingAddress = draft.billingAddressMatchesShippingAddress,
                completedAt = draft.completedAt?.toString(),
                createdAt = draft.createdAt?.toString(),
                currencyCode = draft.currencyCode?.toString(),
                defaultCursor = draft.defaultCursor?.toString(),
                discountCodes = draft.discountCodes ?: emptyList(),
                email = draft.email,
                hasTimelineComment = draft.hasTimelineComment,
                id = draft.id,
                invoiceEmailTemplateSubject = draft.invoiceEmailTemplateSubject,
                invoiceSentAt = draft.invoiceSentAt?.toString(),
                invoiceUrl = draft.invoiceUrl?.toString(),
                legacyResourceId = draft.legacyResourceId?.toString(),
                marketName = draft.marketName,
                marketRegionCountryCode = draft.marketRegionCountryCode?.toString(),
                name = draft.name,
                note2 = draft.note2,
                phone = draft.phone,
                poNumber = draft.poNumber,
                presentmentCurrencyCode = draft.presentmentCurrencyCode?.toString(),
                ready = draft.ready,
                reserveInventoryUntil = draft.reserveInventoryUntil?.toString(),
                status = draft.status?.toString(),
                subtotalPrice = draft.subtotalPrice?.toString()?.toDoubleOrNull(),
                tags = draft.tags?.toList(),
                taxExempt = draft.taxExempt,
                taxesIncluded = draft.taxesIncluded,
                totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                totalQuantityOfLineItems = draft.totalQuantityOfLineItems,
                totalShippingPrice = draft.totalShippingPrice?.toString()?.toDoubleOrNull(),
                totalTax = draft.totalTax?.toString()?.toDoubleOrNull(),
                totalWeight = draft.totalWeight?.toString()?.toDoubleOrNull(),
                transformerFingerprint = draft.transformerFingerprint,
                updatedAt = draft.updatedAt?.toString(),
                visibleToCustomer = draft.visibleToCustomer,

                userErrors = response.data?.draftOrderCreate?.userErrors?.map {
                    UserError(field = it.field?.toString(), message = it.message)
                },
                lineItems = draft.lineItems?.let { lineItems ->
                    DraftOrderLineItemConnection(
                        nodes = lineItems.nodes?.map { node ->
                            LineItem(
                                custom = node.custom,
                                discountedTotal = node.discountedTotal?.toString()?.toDoubleOrNull(),
                                discountedUnitPrice = node.discountedUnitPrice?.toString()?.toDoubleOrNull(),
                                grams = node.grams,
                                id = node.id,
                                isGiftCard = node.isGiftCard,
                                name = node.name,
                                originalTotal = node.originalTotal?.toString()?.toDoubleOrNull(),
                                originalUnitPrice = node.originalUnitPrice?.toString()?.toDoubleOrNull(),
                                quantity = node.quantity,
                                requiresShipping = node.requiresShipping,
                                sku = node.sku,
                                taxable = node.taxable,
                                title = node.title,
                                totalDiscount = node.totalDiscount?.toString()?.toDoubleOrNull(),
                                uuid = node.uuid,
                                variantTitle = node.variantTitle,
                                vendor = node.vendor,
                                image = node.image?.let { image ->
                                    Image(
                                        url = image.url?.toString()
                                    )
                                },
                                variantId = node.variant?.id
                            )
                        }
                    )
                }
            )
        } ?: throw Exception("Failed to create draft order: No response data")

        return draftOrder
    }

    override suspend fun getDraftOrderById(id: String): Flow<List<DraftOrder>>? = flow {
        val response = shopifyService.query(GetDraftOrdersQuery(Optional.present(id))).execute()

        val draftOrder = response.data?.draftOrders?.nodes?.map { draft ->
            DraftOrder(
                acceptAutomaticDiscounts = draft.acceptAutomaticDiscounts,
                allowDiscountCodesInCheckout = draft.allowDiscountCodesInCheckout,
                billingAddressMatchesShippingAddress = draft.billingAddressMatchesShippingAddress,
                completedAt = draft.completedAt?.toString(),
                createdAt = draft.createdAt?.toString(),
                currencyCode = draft.currencyCode?.toString(),
                defaultCursor = draft.defaultCursor,
                discountCodes = draft.discountCodes,
                email = draft.email,
                hasTimelineComment = draft.hasTimelineComment,
                id = draft.id,
                invoiceEmailTemplateSubject = draft.invoiceEmailTemplateSubject,
                invoiceSentAt = draft.invoiceSentAt?.toString(),
                invoiceUrl = draft.invoiceUrl?.toString(),
                legacyResourceId = draft.legacyResourceId?.toString(),
                marketName = draft.marketName,
                marketRegionCountryCode = draft.marketRegionCountryCode?.toString(),
                name = draft.name,
                note2 = draft.note2,
                phone = draft.phone,
                poNumber = draft.poNumber,
                presentmentCurrencyCode = draft.presentmentCurrencyCode?.toString(),
                ready = draft.ready,
                reserveInventoryUntil = draft.reserveInventoryUntil?.toString(),
                status = draft.status?.toString(),
                subtotalPrice = draft.subtotalPrice?.toString()?.toDoubleOrNull(),
                tags = draft.tags,
                taxExempt = draft.taxExempt,
                taxesIncluded = draft.taxesIncluded,
                totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                totalQuantityOfLineItems = draft.totalQuantityOfLineItems,
                totalShippingPrice = draft.totalShippingPrice?.toString()?.toDoubleOrNull(),
                totalTax = draft.totalTax?.toString()?.toDoubleOrNull(),
                totalWeight = draft.totalWeight?.toString()?.toDoubleOrNull(),
                transformerFingerprint = draft.transformerFingerprint,
                updatedAt = draft.updatedAt?.toString(),
                visibleToCustomer = draft.visibleToCustomer,
                lineItems = draft.lineItems?.let { lineItems ->
                    DraftOrderLineItemConnection(
                        nodes = lineItems.nodes?.map { node ->
                            LineItem(
                                custom = node.custom,
                                discountedTotal = node.discountedTotal?.toString()?.toDoubleOrNull(),
                                discountedUnitPrice = node.discountedUnitPrice?.toString()?.toDoubleOrNull(),
                                grams = node.grams,
                                id = node.id,
                                isGiftCard = node.isGiftCard,
                                name = node.name,
                                originalTotal = node.originalTotal?.toString()?.toDoubleOrNull(),
                                originalUnitPrice = node.originalUnitPrice?.toString()?.toDoubleOrNull(),
                                quantity = node.quantity,
                                requiresShipping = node.requiresShipping,
                                sku = node.sku,
                                taxable = node.taxable,
                                title = node.title,
                                totalDiscount = node.totalDiscount?.toString()?.toDoubleOrNull(),
                                uuid = node.uuid,
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
                                                } ?: emptyList()
                                            )
                                        }
                                    )
                                },
                                variantId = node.variant?.id

                            )
                        }
                    )
                },
                billingAddress = draft.billingAddress?.let { address ->
                    BillingAddress(
                        address1 = address.address1,
                        address2 = address.address2,
                        city = address.city,
                        phone = address.phone
                    )
                },
                )
        } ?: throw Exception("Failed to fetch the Draft Order: No response data")

        emit(draftOrder)
    }

    private fun sanitizeVariantId(variantId: String): String {
        return when {
            variantId.contains("gid://shopify/ProductVariant/gid://shopify/DraftOrderLineItem/") -> {
                // Extract just the ID
                val id = variantId.substringAfterLast("/DraftOrderLineItem/")
                "gid://shopify/ProductVariant/$id"
            }
            variantId.contains("Present(value=") -> {
                // Extract value from Present wrapper
                variantId.substringAfter("Present(value=").substringBefore(")")
            }
            !variantId.startsWith("gid://shopify/ProductVariant/") -> {
                // Add proper GID format
                "gid://shopify/ProductVariant/$variantId"
            }
            else -> variantId
        }
    }

    override suspend fun updateDraftOrder(
        id: String,
        lineItems: List<Item>
    ): DraftOrder {

        val draftOrderLineItems = lineItems.map { item ->
            val sanitizedVariantId = sanitizeVariantId(item.variantID)
            Log.d("DraftOrderRemoteDataSourceImp", "LineItem: variantId=$sanitizedVariantId, quantity=${item.quantity}")

            DraftOrderLineItemInput(
                variantId = Optional.present(sanitizedVariantId),
                quantity = item.quantity ?: 1
            )
        }

        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(
                DraftOrderUpdateMutation(
                    id = id,
                    lineItems = draftOrderLineItems
                )
            ).execute()
        }

        Log.d("DraftOrderUpdate", "Request ID: $id")
        Log.d("DraftOrderUpdate", "LineItems: $draftOrderLineItems")
        Log.d("DraftOrderUpdate", "Response: ${response.data?.draftOrderUpdate?.userErrors}")

        response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.let { error ->
            Log.e("DraftOrderUpdate", "Error: ${error.message}")
        }

        return response.data?.draftOrderUpdate?.draftOrder?.let { draft ->
            DraftOrder(
                id = draft.id,
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
                },
                billingAddress = draft.billingAddress?.let { address ->
                    BillingAddress(
                        address1 = address.address1,
                        address2 = address.address2,
                        city = address.city,
                        phone = address.phone
                    )
                }
            )
        } ?: throw Exception("Failed to update draft order: ${response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.message ?: "No response data"}")
    }

    override suspend fun updateDraftOrderBillingAddress(
        id: String,
        billingAddress: BillingAddress
    ): DraftOrder {
        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(
                UpdateDraftOrderBillingAddressMutation(
                    id = id,
                    billingAddress = Optional.presentIfNotNull(billingAddress?.let { billingAddress ->
                        MailingAddressInput(
                            address1 = Optional.Present(billingAddress.address1),
                            address2 = Optional.Present(billingAddress.address2),
                            city = Optional.Present(billingAddress.city),
                            phone = Optional.Present(billingAddress.phone),
                        )
                    })
                )
            ).execute()
        }

        response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.let { error ->
            Log.e("DraftOrderUpdate", "Error: ${error.message}")
        }
        Log.d(
            "DraftOrderUpdate",
            "Draft order updated successfully ${response.data?.draftOrderUpdate?.draftOrder?.billingAddress}"
        )
        return (response.data?.draftOrderUpdate?.draftOrder?.let { draft ->
            DraftOrder(
                id = draft.id,
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
                },
                billingAddress = draft.billingAddress?.let { address ->
                    BillingAddress(
                        address1 = address.address1,
                        address2 = address.address2,
                        city = address.city,
                        phone = address.phone
                    )
                }
            )
        } ?: Log.e(
            "DraftOrderUpdate",
            "Draft order update failed ${response.data?.draftOrderUpdate?.userErrors?.firstOrNull()}"
        )) as DraftOrder
    }

    override suspend fun deleteDraftOrder(id: String): Boolean {
        return try {

            val response = withContext(Dispatchers.IO) {
                shopifyService.mutation(
                    DraftOrderDeleteMutation(id = id)
                ).execute()
            }

            val success = response.data?.draftOrderDelete?.userErrors?.isEmpty() ?: false
            response.data?.draftOrderDelete?.userErrors?.forEach { error ->
                Log.e("DraftOrderDelete", "Error: ${error.message}, Field: ${error.field}")
            }
            Log.e("DraftOrderDelete", "${response.data?.draftOrderDelete?.userErrors} + $success" )

            success
        } catch (e: Exception) {
            Log.e("DraftOrderDelete", "Failed to delete draft order", e)
            false
        }
    }

    override suspend fun completeAndDeleteDraftOrder(draftOrderId: String): Boolean {
        return try {

            val response = withContext(Dispatchers.IO) {
                shopifyService.mutation(
                    CompleteDraftOrderMutation(id = draftOrderId)
                ).execute()
            }

            val userErrors = response.data?.draftOrderComplete?.userErrors.orEmpty()

            if (userErrors.isNotEmpty()) {
                userErrors.forEach {
                }
                return false
            }

            val deletionSuccess = deleteDraftOrder(draftOrderId)
            deletionSuccess
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateDraftOrderApplyVoucher(
        id: String,
        discountCode: List<String>
    ): DraftOrder {
        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(
                UpdateDraftOrderApplyDiscountCodeMutation(
                    id = id,
                    discountCodes = Optional.presentIfNotNull(discountCode)
                )
            ).execute()
        }

        response.data?.draftOrderUpdate?.userErrors?.firstOrNull()?.let { error ->
            Log.e("DraftOrderUpdate", "Error: ${error.message}")
        }
        Log.i("DraftOrderUpdate", "Response: ${response.data?.draftOrderUpdate?.draftOrder?.discountCodes}")

        return (response.data?.draftOrderUpdate?.draftOrder?.let { draft ->
            DraftOrder(
                id = draft.id,
                name = draft.name,
                status = draft.status?.toString(),
                discountCodes = draft.discountCodes,
                totalPrice = draft.totalPrice?.toString()?.toDoubleOrNull(),
                totalDiscountsSet = draft.totalDiscountsSet?.presentmentMoney?.amount.toString(),
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
                },

            )
        } ?: Log.e(
            "DraftOrderUpdate",
            "Draft order update failed ${response.data?.draftOrderUpdate?.userErrors?.firstOrNull()}"
        )) as DraftOrder
    }
}

