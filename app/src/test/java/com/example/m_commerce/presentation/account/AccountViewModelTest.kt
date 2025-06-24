package com.example.m_commerce.presentation.account

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.CustomerUseCase
import io.mockk.mockk
import org.junit.Before
import com.example.m_commerce.domain.entities.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {

    lateinit var viewModel: AccountViewModel
    lateinit var useCase: CustomerUseCase
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val testDispatcher = StandardTestDispatcher()
    val customer = Customer(
        id = "1",
        displayName = "Tasneem Mohamed",
        email = "tasneem@gmail.com",
        firstName = "Tasneem",
        lastName = "Mohamed",
        addresses = emptyList(),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk(relaxed = true)
        sharedPreferencesHelper = mockk(relaxed = true)
        viewModel = AccountViewModel(
            useCase,
            sharedPreferencesHelper,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCustomerData should update customerState and isLoading`() = runTest {
        coEvery { useCase.invoke(any()) } returns flowOf(customer)
        viewModel.getCustomerData("1")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.customerState.first()
        val loading = viewModel.isLoading.first()
        assertEquals(customer, state)
        assertFalse(loading)
    }

    @Test
    fun `getCustomerData should set error on exception`() = runTest {
        val errorMsg = "Network error"
        coEvery { useCase.invoke(any()) } returns flow { throw Exception(errorMsg) }
        viewModel.getCustomerData("1")
        testDispatcher.scheduler.advanceUntilIdle()
        val error = viewModel.error.first()
        val loading = viewModel.isLoading.first()
        assertEquals(errorMsg, error)
        assertFalse(loading)
    }

    @Test
    fun `init should call getCustomerData with id from SharedPreferencesHelper`() = runTest {
        coEvery { useCase.invoke(any()) } returns flowOf(customer)
        every { sharedPreferencesHelper.getCustomerId() } returns 1L.toString()
        val vm = AccountViewModel(useCase, sharedPreferencesHelper)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.customerState.first()
        assertEquals("1", state?.id)
    }
}