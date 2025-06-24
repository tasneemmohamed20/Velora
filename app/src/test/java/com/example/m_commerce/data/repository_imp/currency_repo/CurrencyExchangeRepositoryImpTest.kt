package com.example.m_commerce.data.repository_imp.currency_repo

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.data.datasource.remote.restful.currency_exchange.FakeCurrencyExchangeDataSource
import com.example.m_commerce.domain.repository.ICurrencyExchangeRepository
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class CurrencyExchangeRepositoryImpTest {

    @Mock
    private lateinit var mockSharedPreferencesHelper: SharedPreferencesHelper

    private lateinit var fakeRemoteDataSource: FakeCurrencyExchangeDataSource
    private lateinit var repository: ICurrencyExchangeRepository

    @Before
    fun setUp() {
        mockSharedPreferencesHelper = mockk(relaxed = true)
        fakeRemoteDataSource = FakeCurrencyExchangeDataSource()
        repository =
            CurrencyExchangeRepositoryImp(fakeRemoteDataSource, mockSharedPreferencesHelper)
    }

    @Test
    fun getCurrencyExchangeRate_returnsCurrencyExchangeResponse() = runTest {
        // When
        val result = repository.getCurrencyExchangeRate().first()

        // Then
        assertEquals("USD", result.base)
        assertEquals("2025-06-24", result.date)
        assertEquals("50.68", result.rates.EGP)
    }
}
