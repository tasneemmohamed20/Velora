package com.example.m_commerce.domain.usecases


import com.example.m_commerce.domain.repository.IAuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String) =
        authRepository.signUp(email, password, firstName, lastName)

    suspend fun login(email: String, password: String) =
        authRepository.login(email, password)
}