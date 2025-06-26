package com.example.m_commerce.presentation.authentication.login

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.AuthUseCase
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var authUseCase: AuthUseCase
    private lateinit var sharedPrefs: SharedPreferencesHelper
    private lateinit var draftOrderUseCase: DraftOrderUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        authUseCase = mockk()
        sharedPrefs = mockk(relaxed = true)
        draftOrderUseCase = mockk()

        viewModel = LoginViewModel(authUseCase, sharedPrefs, draftOrderUseCase)
    }

    @Test
    fun `login with empty email or password should return Failure`() = runTest {
        viewModel.login("", "")
        assert(viewModel.loginState.value is ResponseState.Failure)
    }

    @Test
    fun `login with valid credentials should return Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "123456"

        coEvery { authUseCase.login(email, password) } returns Result.success(Unit)
        every { sharedPrefs.getCustomerEmail() } returns email
        coEvery { draftOrderUseCase(email) } returns emptyFlow()

        // When
        viewModel.login(email, password)

        // Then
        advanceUntilIdle()

        val result = viewModel.loginState.value
        assert(result is ResponseState.Success)
        assert((result as ResponseState.Success).data == "Welcome back! You have successfully logged in")
    }

    @Test
    fun `login should return Failure on authUseCase error`() = runTest {
        val email = "test@example.com"
        val password = "wrongpass"

        coEvery { authUseCase.login(email, password) } returns Result.failure(Exception("Invalid credentials"))

        // When
        viewModel.login(email, password)

        // Then
        advanceUntilIdle()
        val result = viewModel.loginState.value
        assert(result is ResponseState.Failure)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
