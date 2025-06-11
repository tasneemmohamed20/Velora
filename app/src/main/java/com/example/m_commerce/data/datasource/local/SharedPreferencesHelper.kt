package com.example.m_commerce.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesHelper(context: Context) {
    private val TAG = "SharedPreferencesHelper"
    private val PREFS_NAME = "MyCommercePrefs"
    private val CUSTOMER_ID_KEY = "customer_id"
    private val CUSTOMER_Token = "customer_token"
    private val CURRENCY_KEY = "currency_is_egp"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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
    fun saveCurrencyExchangeRate(isEGP: Boolean) {
        prefs.edit().apply {
            putBoolean(CURRENCY_KEY, isEGP)
            apply()
        }
        Log.d(TAG, "Saved currency preference: EGP = $isEGP")
    }

    fun getCurrencyIsEGP(): Boolean {
        return prefs.getBoolean(CURRENCY_KEY, true) // Default to EGP
    }

}