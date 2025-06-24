package com.example.m_commerce.presentation.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadFavorites() {
        viewModelScope.launch {
            val email = sharedPreferencesHelper.getCustomerEmail()
            if (email == null) {
                _favoriteProducts.value = emptyList()
                return@launch
            }

            _isLoading.value = true
            try {
                favoriteProductsUseCases.getFavoriteDraftOrders(note.fav.name).collect { draftOrders ->
                    val matchingDraftOrders = draftOrders.filter {
                        it.note2 == note.fav.name
                    }

                    val allProducts = matchingDraftOrders.flatMap { draftOrder ->
                        draftOrder.lineItems?.nodes?.mapNotNull { lineItem ->
                            lineItemToProduct(lineItem)
                        } ?: emptyList()
                    }

                    val uniqueProducts = allProducts.distinctBy { it.id }

                    android.util.Log.d("FavoriteViewModel", "Draft Orders: ${matchingDraftOrders.size}")
                    android.util.Log.d("FavoriteViewModel", "Total Products: ${uniqueProducts.size}")

                    _favoriteProducts.value = uniqueProducts

                    if (matchingDraftOrders.isNotEmpty()) {
                        sharedPreferencesHelper.saveFavoriteDraftOrderId(
                            matchingDraftOrders.first().id.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("FavoriteViewModel", "Error loading favorites", e)
                _favoriteProducts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }

    fun updateFavoriteDraftOrder(draftOrderId: String, lineItems: List<Item>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val updatedDraftOrder = favoriteProductsUseCases.updateDraftOrder(draftOrderId, lineItems)
                refreshFavorites()
            } catch (e: Exception) {
                android.util.Log.e("FavoriteViewModel", "Error updating draft order", e)
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
                    selectedOptions = variant.selectedOptions?.map { option ->
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

    fun removeProductFromFavorites(variantIdToRemove: String) {
        viewModelScope.launch {
            val draftOrderId = sharedPreferencesHelper.getFavoriteDraftOrderId()
            val currentProducts = favoriteProducts.value
            android.util.Log.d("FavoriteViewModel", "Current product variant IDs: ${currentProducts.map { it.variants.firstOrNull()?.id }}")
            android.util.Log.d("FavoriteViewModel", "Variant ID to remove: $variantIdToRemove")
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
                    android.util.Log.d("FavoriteViewModel", "Deleting draft order: $draftOrderId")
                    favoriteProductsUseCases.deleteDraftOrder(draftOrderId)
                    sharedPreferencesHelper.saveFavoriteDraftOrderId("")
                    refreshFavorites()
                } else {
                    android.util.Log.d("FavoriteViewModel", "Updating draft order: $draftOrderId with items: $updatedItems")
                    updateFavoriteDraftOrder(draftOrderId, updatedItems)
                }
            }
        }
    }
}