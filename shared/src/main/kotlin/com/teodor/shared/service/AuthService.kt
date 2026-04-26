package com.teodor.shared.service

import com.teodor.shared.domain.entities.User

interface AuthService {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(newUser: User, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
}