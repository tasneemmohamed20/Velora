package com.example.m_commerce.presentation.favorite

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.ProductVariant
import com.example.m_commerce.domain.entities.SelectedOption
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.domain.usecases.FavoriteProductsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteProductsUseCases: FavoriteProductsUseCases,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteVariantIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteVariantIds: StateFlow<Set<String>> = _favoriteVariantIds

    fun loadFavorites() {
        viewModelScope.launch {
            val email = sharedPreferencesHelper.getCustomerEmail()
            if (email == null) {
                _favoriteProducts.value = emptyList()
                _favoriteVariantIds.value = emptySet()
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = true
            try {

                favoriteProductsUseCases.getFavoriteDraftOrders(email).collect { draftOrders ->

                    val matchingDraftOrders = draftOrders.filter { draftOrder ->
                        val matches = draftOrder.email == email && draftOrder.note2 == note.fav.name
                        matches
                    }

                    val allProducts = matchingDraftOrders.flatMap { draftOrder ->
                        draftOrder.lineItems?.nodes?.mapNotNull { lineItem ->
                            lineItemToProduct(lineItem)
                        } ?: emptyList()
                    }

                    val uniqueProducts = allProducts.distinctBy { it.id }

                    val variantIds = uniqueProducts.flatMap { product ->
                        product.variants.map { it.id }
                    }.toSet()

                    _favoriteProducts.value = uniqueProducts
                    _favoriteVariantIds.value = variantIds


                    if (matchingDraftOrders.isNotEmpty()) {
                        sharedPreferencesHelper.saveFavoriteDraftOrderId(
                            matchingDraftOrders.first().id.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                _favoriteProducts.value = emptyList()
                _favoriteVariantIds.value = emptySet()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }

    fun isProductFavorited(variantId: String): Boolean {
        return _favoriteVariantIds.value.contains(variantId)
    }

    fun toggleProductFavorite(product: Product, variantId: String) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorited = isProductFavorited(variantId)

                if (isCurrentlyFavorited) {
                    _favoriteVariantIds.value = _favoriteVariantIds.value - variantId
                    _favoriteProducts.value = _favoriteProducts.value.filter {
                        it.variants.firstOrNull()?.id != variantId
                    }
                } else {
                    _favoriteVariantIds.value = _favoriteVariantIds.value + variantId
                    _favoriteProducts.value = _favoriteProducts.value + product
                }

                _isLoading.value = true

                try {
                    if (isCurrentlyFavorited) {
                        removeProductFromFavorites(variantId)
                    } else {
                        addProductToFavorites(product, variantId)
                    }
                } catch (e: Exception) {
                    if (isCurrentlyFavorited) {
                        _favoriteVariantIds.value = _favoriteVariantIds.value + variantId
                        _favoriteProducts.value = _favoriteProducts.value + product
                    } else {
                        _favoriteVariantIds.value = _favoriteVariantIds.value - variantId
                        _favoriteProducts.value = _favoriteProducts.value.filter {
                            it.variants.firstOrNull()?.id != variantId
                        }
                    }
                    throw e
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error toggling favorite", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun addProductToFavorites(product: Product, variantId: String) {
        val email = sharedPreferencesHelper.getCustomerEmail()
        if (email == null) {
            Log.e("FavoriteViewModel", "No customer email found")
            return
        }

        try {
            val existingDraftOrderId = sharedPreferencesHelper.getFavoriteDraftOrderId()

            if (existingDraftOrderId.isNullOrEmpty()) {
                val newDraftOrder = favoriteProductsUseCases.addToFavorites(email, variantId, 1)
                sharedPreferencesHelper.saveFavoriteDraftOrderId(newDraftOrder.id.toString())
            } else {
                val currentProducts = favoriteProducts.value
                val updatedItems = currentProducts.map { existingProduct ->
                    Item(
                        variantID = existingProduct.variants.firstOrNull()?.id.orEmpty(),
                        quantity = 1
                    )
                }.toMutableList()

                updatedItems.add(Item(variantID = variantId, quantity = 1))

                favoriteProductsUseCases.updateDraftOrder(existingDraftOrderId, updatedItems)
            }
        } catch (e: Exception) {
            Log.e("FavoriteViewModel", "Error adding product to favorites: ${e.message}")
            throw e
        }
    }

    private fun updateFavoriteDraftOrder(draftOrderId: String, lineItems: List<Item>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                favoriteProductsUseCases.updateDraftOrder(draftOrderId, lineItems)
                refreshFavorites()
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error updating draft order", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun lineItemToProduct(lineItem: LineItem): Product {
        val isUsd = sharedPreferencesHelper.getCurrencyPreference()
        val usdToEgp = sharedPreferencesHelper.getUsdToEgpValue()
        val originalPrice = lineItem.originalUnitPrice ?: 0.0
        val (amount, currencyCode) = if (isUsd) {
            originalPrice.toString() to "USD"
        } else {
            (originalPrice * usdToEgp).toString() to "EGP"
        }

        return Product(
            id = lineItem.product?.id ?: "",
            title = lineItem.title ?: "",
            productType = lineItem.product?.productType ?: "",
            description = lineItem.name ?: "",
            price = PriceDetails(
                minVariantPrice = Price(
                    amount = amount,
                    currencyCode = currencyCode
                )
            ),
            images = listOfNotNull(lineItem.image?.url),
            variants = lineItem.product?.variants?.map { variant ->
                ProductVariant(
                    id = variant.id,
                    title = variant.title,
                    availableForSale = variant.availableForSale,
                    selectedOptions = variant.selectedOptions?.mapNotNull { option ->
                        option?.let {
                            SelectedOption(
                                name = it.name,
                                value = it.value
                            )
                        }
                    }
                )
            } ?: emptyList()
        )
    }

    private fun removeProductFromFavorites(variantIdToRemove: String) {
        viewModelScope.launch {
            try {
                val draftOrderId = sharedPreferencesHelper.getFavoriteDraftOrderId()
                val currentProducts = favoriteProducts.value

                val updatedItems = currentProducts
                    .filter { it.variants.firstOrNull()?.id != variantIdToRemove }
                    .map { product ->
                        Item(
                            variantID = product.variants.firstOrNull()?.id.orEmpty(),
                            quantity = 1
                        )
                    }

                if (draftOrderId != null) {
                    if (updatedItems.isEmpty()) {
                        favoriteProductsUseCases.deleteDraftOrder(draftOrderId)
                        sharedPreferencesHelper.saveFavoriteDraftOrderId("")
                    } else {
                        updateFavoriteDraftOrder(draftOrderId, updatedItems)
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error removing product from favorites: ${e.message}")
            }
        }
    }
}