package com.example.m_commerce.presentation.account.settings.view_model

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.Rates
import com.example.m_commerce.domain.usecases.CurrencyExchangeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    lateinit var usecase: CurrencyExchangeUseCase
    lateinit var sharedPref: SharedPreferencesHelper
    lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        usecase = mockk(relaxed = true)
        sharedPref = mockk(relaxed = true)
        settingsViewModel = SettingsViewModel(usecase, sharedPref)
    }

    @Test
    fun `getCurrencyPreference should return value from SharedPreferencesHelper`() {
        every { sharedPref.getCurrencyPreference() } returns false
        val result = settingsViewModel.getCurrencyPreference()
        assertEquals(false, result)
    }

    @Test
    fun `saveUsdToEgpValue should call SharedPreferencesHelper with correct value`() {
        settingsViewModel.saveUsdToEgpValue(30.5f)
        verify { sharedPref.saveUsdToEgpValue(30.5f) }
    }

    @Test
    fun `getCurrencyExchange should emit response and save USD to EGP value`() = runTest {
        val response = CurrencyExchangeResponse(
            date ="2025-06-24",
            rates = Rates(
                EGP = "50.0"
            ),
            base = "USD"
        )
        coEvery { usecase.invoke() } returns flowOf(response)

        settingsViewModel.getCurrencyExchange()

        val emitted = settingsViewModel.currencyExchange.first()
        assertEquals(response, emitted)
        coVerify { sharedPref.saveUsdToEgpValue(50.0f) }
    }

}