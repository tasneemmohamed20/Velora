import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.AuthUseCase
import com.example.m_commerce.presentation.authentication.signUp.SignUpViewModel
import com.example.m_commerce.presentation.utils.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import io.mockk.*

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authUseCase: AuthUseCase
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authUseCase = mockk()
        sharedPreferencesHelper = mockk(relaxed = true)
        viewModel = SignUpViewModel(authUseCase, sharedPreferencesHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signUp with valid input should update state to Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "123456"
        val firstName = "Suhaila"
        val lastName = "Farahat"

        coEvery {
            authUseCase.signUp(email, password, firstName, lastName)
        } returns Result.success(Unit)

        // When
        viewModel.signUp(email, password, password, firstName, lastName)

        advanceUntilIdle()

        // Then
        val state = viewModel.signUpState.value
        assertTrue(state is ResponseState.Success)
        assertEquals("Account created successfully", (state as ResponseState.Success).data)
    }

    @Test
    fun `signUp with empty fields should return Failure`() {
        // When
        viewModel.signUp("", "", "", "", "")

        // Then
        val state = viewModel.signUpState.value
        assertTrue(state is ResponseState.Failure)
        assertEquals("Please fill in all fields", (state as ResponseState.Failure).err.message)
    }

    @Test
    fun `signUp with mismatched passwords should return Failure`() {
        // When
        viewModel.signUp("email@test.com", "pass1", "pass2", "First", "Last")

        // Then
        val state = viewModel.signUpState.value
        assertTrue(state is ResponseState.Failure)
        assertEquals("Passwords do not match", (state as ResponseState.Failure).err.message)
    }

}






