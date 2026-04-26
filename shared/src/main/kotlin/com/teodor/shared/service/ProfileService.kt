package com.teodor.shared.service

import com.teodor.shared.domain.entities.User

interface ProfileService {
    suspend fun updateProfile(newUser: User, newEmail: String?, newPassword: String?): Result<Unit>
    suspend fun deleteAccount(id: String): Result<Unit>
}