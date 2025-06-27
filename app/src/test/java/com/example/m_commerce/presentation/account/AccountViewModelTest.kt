package com.example.m_commerce.presentation.account

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.mockk
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
import org.junit.Before
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
        every { sharedPreferencesHelper.getCurrentUserMode() } returns "Customer"
        every { sharedPreferencesHelper.getCustomerId() } returns "1"
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
    fun `getCustomerData should update customerState to Success`() = runTest {
        coEvery { useCase.invoke(any()) } returns flowOf(customer)
        viewModel.getCustomerData("1")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.customerState.first()
        assertTrue(state is ResponseState.Success)
        assertEquals(customer, (state as ResponseState.Success).data)
    }

    @Test
    fun `getCustomerData should set error on exception`() = runTest {
        val exception = Exception("Network error")
        coEvery { useCase.invoke(any()) } returns flow { throw exception }
        viewModel.getCustomerData("1")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.customerState.first()
        assertTrue(state is ResponseState.Failure)
        assertEquals(exception, (state as ResponseState.Failure).err)
    }

    @Test
    fun `init should call getCustomerData with id from SharedPreferencesHelper`() = runTest {
        coEvery { useCase.invoke(any()) } returns flowOf(customer)
        every { sharedPreferencesHelper.getCustomerId() } returns "1"
        val vm = AccountViewModel(useCase, sharedPreferencesHelper)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.customerState.first()
        assertTrue(state is ResponseState.Success)
        assertEquals(customer, (state as ResponseState.Success).data)
    }

    @Test
    fun `init should set Success state for Guest user`() = runTest {
        every { sharedPreferencesHelper.getCurrentUserMode() } returns "Guest"
        val vm = AccountViewModel(useCase, sharedPreferencesHelper)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = vm.customerState.first()
        assertTrue(state is ResponseState.Success)
    }
}
