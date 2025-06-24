package com.example.m_commerce.domain.repository


interface IAuthRepository {
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
}