package com.example.m_commerce.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(context: Context) {
    private val TAG = "SharedPreferencesHelper"
    private val PREFS_NAME = "MyCommercePrefs"
    private val CUSTOMER_ID_KEY = "customer_id"
    private val CUSTOMER_Token = "customer_token"
    private val CURRENCY_KEY = "currency_is_egp"
    private val CART_ID_KEY = "cart_id"
    private val FAVORITE_DRAFT_ORDER_ID_KEY = "favorite_draft_order_id"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val IS_GUEST_MODE_KEY = "is_guest_mode"


    fun saveCustomerId(customerId: String) {
        prefs.edit().apply {
            putString(CUSTOMER_ID_KEY, customerId)
            apply()
        }
        Log.d(TAG, "Saved customer ID: $customerId")
    }

    fun saveCustomerToken(customerToken: String) {
        prefs.edit().apply {
            putString(CUSTOMER_Token, customerToken)
            apply()
        }
        Log.d(TAG, "Saved customer token: $customerToken")
    }

    fun getCustomerToken(): String? {
        return prefs.getString(CUSTOMER_Token, null)
    }

    fun getCustomerId(): String? {
        return prefs.getString(CUSTOMER_ID_KEY, null)
    }

    fun clearCustomerId() {
        prefs.edit().apply {
            remove(CUSTOMER_ID_KEY)
            apply()
        }
    }

    // currency exchange
    fun isUSD(isUSD: Boolean) : Boolean {
        prefs.edit().apply {
            putBoolean(CURRENCY_KEY, isUSD)
            apply()
        }
        Log.d(TAG, "Shared Pref: $isUSD")
        return isUSD
    }

    fun saveUsdToEgpValue(value: Float) {
        prefs.edit().apply {
            putFloat("UsdToEgp", value)
            apply()
        }
    }

    fun getUsdToEgpValue() : Float{
        return prefs.getFloat("UsdToEgp", 0f)
    }
    fun getCurrencyPreference(): Boolean {
        return prefs.getBoolean(CURRENCY_KEY, false)
    }

    fun saveCartDraftOrderId(draftOrderId: String) {
        prefs.edit().apply {
            putString(CART_ID_KEY, draftOrderId)
            apply()
        }
        Log.d(TAG, "Saved cart draft order ID: $draftOrderId")
    }

    fun getCartDraftOrderId(): String? {
        return prefs.getString(CART_ID_KEY, null)
    }

    fun saveCustomerEmail(email: String) {
        prefs.edit().apply {
            putString("customer_email", email)
            apply()
        }
        Log.d(TAG, "Saved customer email: $email")
    }

    fun removeKey(key: String) {
        prefs.edit().apply {
            remove(key)
            apply()
        }

    }


    fun getCustomerEmail(): String? {
        Log.d(TAG, "getCustomerEmail: ${prefs.getString("customer_email", null)}")
        return prefs.getString("customer_email", null)
    }

    fun saveFavoriteDraftOrderId(draftOrderId: String) {
        prefs.edit().apply {
            putString(FAVORITE_DRAFT_ORDER_ID_KEY, draftOrderId)
            apply()
        }
    }

    fun getFavoriteDraftOrderId(): String? {
        return prefs.getString(FAVORITE_DRAFT_ORDER_ID_KEY, null)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun setGuestMode(isGuest: Boolean) {
        prefs.edit().apply {
            putBoolean(IS_GUEST_MODE_KEY, isGuest)
            apply()
        }
        Log.d(TAG, "Guest mode set to: $isGuest")
    }

    fun isGuestMode(): Boolean {
        val isGuest = prefs.getBoolean(IS_GUEST_MODE_KEY, false)
        Log.d(TAG, "Is guest mode: $isGuest")
        return isGuest
    }

    fun clearGuestMode() {
        prefs.edit().apply {
            remove(IS_GUEST_MODE_KEY)
            apply()
        }
        Log.d(TAG, "Guest mode cleared")
    }

    fun isUserAuthenticated(): Boolean {
        return !isGuestMode() && (getCustomerId() != null || getCustomerEmail() != null)
    }

    fun getCurrentUserMode(): String {
        return when {
            isGuestMode() -> "Guest"
            isUserAuthenticated() -> "Authenticated"
            else -> "Unknown"
        }
    }

}