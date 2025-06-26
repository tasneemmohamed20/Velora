package com.example.m_commerce.start

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.repository.ICustomerRepository
import com.example.m_commerce.presentation.utils.ResponseState
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*


@ExperimentalCoroutinesApi
class StartViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: StartViewModel
    private lateinit var mockSharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var mockCustomerRepo: ICustomerRepository
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockAuthResult: AuthResult
    private lateinit var mockFirebaseUser: FirebaseUser
    private lateinit var mockTask: Task<AuthResult>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockSharedPreferencesHelper = mockk(relaxed = true)
        mockCustomerRepo = mockk(relaxed = true)
        mockFirebaseAuth = mockk(relaxed = true)
        mockAuthResult = mockk(relaxed = true)
        mockFirebaseUser = mockk(relaxed = true)
        mockTask = mockk(relaxed = true)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockFirebaseAuth

        mockkConstructor(OkHttpClient::class)

        viewModel = StartViewModel(mockSharedPreferencesHelper, mockCustomerRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `handleGoogleSignIn failure updates state correctly`() = runTest {
        // Arrange
        val idToken = "test_id_token"
        val exception = Exception("Firebase auth failed")

        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } answers {
            val listener = firstArg<OnFailureListener>()
            listener.onFailure(exception)
            mockTask
        }
        every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

        // Act
        viewModel.handleGoogleSignIn(idToken)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.googleSignInState.value is ResponseState.Failure)
        assertEquals(exception, (viewModel.googleSignInState.value as ResponseState.Failure).err)
    }


    @Test
    fun `handleGuestMode activates guest mode successfully`() = runTest {
        // Act
        viewModel.handleGuestMode()
        advanceUntilIdle()

        // Assert
        verify { mockSharedPreferencesHelper.clearCustomerId() }
        verify { mockSharedPreferencesHelper.removeKey("customer_email") }
        verify { mockSharedPreferencesHelper.setGuestMode(true) }
        assertTrue(viewModel.guestModeState.value is ResponseState.Success)
        assertEquals("Guest mode activated", (viewModel.guestModeState.value as ResponseState.Success).data)
    }

    @Test
    fun `handleGuestMode handles exception correctly`() = runTest {
        // Arrange
        val exception = Exception("SharedPreferences error")
        every { mockSharedPreferencesHelper.setGuestMode(true) } throws exception

        // Act
        viewModel.handleGuestMode()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.guestModeState.value is ResponseState.Failure)
        assertEquals(exception, (viewModel.guestModeState.value as ResponseState.Failure).err)
    }

    @Test
    fun `clearGoogleSignInState resets state to null`() {
        // Arrange
        viewModel.handleGoogleSignIn("test_token")

        // Act
        viewModel.clearGoogleSignInState()

        // Assert
        assertNull(viewModel.googleSignInState.value)
    }

    @Test
    fun `clearGuestModeState resets state to null`() {
        // Arrange
        viewModel.handleGuestMode()

        // Act
        viewModel.clearGuestModeState()

        // Assert
        assertNull(viewModel.guestModeState.value)
    }

    @Test
    fun `getCurrentUserMode returns correct mode`() {
        // Arrange
        val expectedMode = "AUTHENTICATED"
        every { mockSharedPreferencesHelper.getCurrentUserMode() } returns expectedMode

        // Act
        val result = viewModel.getCurrentUserMode()

        // Assert
        assertEquals(expectedMode, result)
        verify { mockSharedPreferencesHelper.getCurrentUserMode() }
    }

    @Test
    fun `switchToAuthenticatedMode clears guest mode`() {
        // Act
        viewModel.switchToAuthenticatedMode()

        // Assert
        verify { mockSharedPreferencesHelper.clearGuestMode() }
    }


    @Test
    fun `loading state is set during google sign in`() = runTest {
        // Arrange
        val idToken = "test_id_token"

        // Don't trigger success/failure immediately
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask
        every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

        // Act
        viewModel.handleGoogleSignIn(idToken)

        // Assert
        assertTrue(viewModel.googleSignInState.value is ResponseState.Loading)
    }
}