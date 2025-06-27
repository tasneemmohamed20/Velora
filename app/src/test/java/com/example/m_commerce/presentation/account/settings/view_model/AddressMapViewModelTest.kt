package com.example.m_commerce.presentation.account.settings.view_model

import android.content.Context
import androidx.work.WorkManager
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AddressMapViewModelTest {
    private lateinit var viewModel: AddressMapViewModel
    private lateinit var geoCodingRepository: IGeoCodingRepository
    private lateinit var placesClient: PlacesClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var customerUseCase: CustomerUseCase
    private lateinit var context: Context
    private lateinit var workManager: WorkManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        geoCodingRepository = mockk(relaxed = true)
        placesClient = mockk(relaxed = true)
        sharedPreferencesHelper = mockk(relaxed = true)
        customerUseCase = mockk(relaxed = true)
        context = mockk(relaxed = true)
        workManager = mockk(relaxed = true)
        every { sharedPreferencesHelper.getCustomerId() } returns 1L.toString()
        viewModel = AddressMapViewModel(
            context,
            geoCodingRepository,
            placesClient,
            sharedPreferencesHelper,
            customerUseCase,
            workManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `resetForAddMode should reset add mode and phone number`() = runTest {
        viewModel.resetForAddMode()
        assertTrue(viewModel.isAddMode.value)
        assertNull(viewModel.editingAddress.value)
        assertEquals("", viewModel.phoneNumber.value)
    }

    @Test
    fun `validateAndUpdatePhone should validate phone number correctly`() = runTest {
        viewModel.validateAndUpdatePhone("")
        assertTrue(viewModel.isPhoneValid.value)
        viewModel.validateAndUpdatePhone("01234567890")
        assertTrue(viewModel.isPhoneValid.value)
        viewModel.validateAndUpdatePhone("123")
        assertFalse(viewModel.isPhoneValid.value)
    }

    @Test
    fun `updateCurrentLocation should update current location and fetch address`() = runTest {
        val latLng = LatLng(30.0, 31.0)
        coEvery { geoCodingRepository.getAddressFromGeocoding(any(), any()) } returns flowOf("Test Address")
        viewModel.updateCurrentLocation(latLng)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(latLng, viewModel.currentLocation.value)
        assertEquals("Test Address", viewModel.address.value)
    }
}