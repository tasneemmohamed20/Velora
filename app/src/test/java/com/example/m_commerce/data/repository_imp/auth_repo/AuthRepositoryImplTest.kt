package com.example.m_commerce.data.repository_imp.auth_repo


import com.example.m_commerce.data.datasource.remote.graphql.auth.AuthDataSource
import com.example.m_commerce.domain.repository.IAuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private lateinit var authDataSource: AuthDataSource
    private lateinit var authRepository: IAuthRepository

    @Before
    fun setUp() {
        authDataSource = mockk()
        authRepository = AuthRepositoryImpl(authDataSource)
    }

    @Test
    fun `signUp should call authDataSource and return success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val firstName = "Suhaila"
        val lastName = "Farahat"

        coEvery {
            authDataSource.signUp(email, password, firstName, lastName)
        } returns Result.success(Unit)

        // When
        val result = authRepository.signUp(email, password, firstName, lastName)

        // Then
        assertTrue(result.isSuccess)
        coVerify { authDataSource.signUp(email, password, firstName, lastName) }
    }

    @Test
    fun `login should call authDataSource and return failure`() = runTest {
        // Given
        val email = "su@example.com"
        val password = "wrongpass"
        val exception = Exception("Invalid credentials")

        coEvery {
            authDataSource.login(email, password)
        } returns Result.failure(exception)

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
        coVerify { authDataSource.login(email, password) }
    }
}
