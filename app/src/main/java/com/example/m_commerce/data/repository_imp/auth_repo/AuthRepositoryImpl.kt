package com.example.m_commerce.data.repository_imp.auth_repo


import com.example.m_commerce.data.datasource.remote.graphql.auth.AuthDataSource
import com.example.m_commerce.domain.repository.IAuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : IAuthRepository {
    override suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Unit> {
        return authDataSource.signUp(email, password, firstName, lastName)
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return authDataSource.login(email, password)
    }
}