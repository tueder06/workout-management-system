package com.teodor.shared.persistence

import com.teodor.shared.domain.entities.User

interface UserRepository : Repository<String, User> {
    suspend fun auth(email: String, password: String): User?
    suspend fun register(user: User, password: String): User
    suspend fun logout()
    suspend fun updateCredentials(newUser: User, newEmail: String?, newPassword: String?)
    suspend fun getCurrentUser(): User?
}