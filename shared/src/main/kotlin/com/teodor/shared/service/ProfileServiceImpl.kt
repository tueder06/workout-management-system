package com.teodor.shared.service

import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.entities.User
import com.teodor.shared.domain.validators.AuthValidator
import com.teodor.shared.persistence.UserRepository
import org.slf4j.LoggerFactory

class ProfileServiceImpl(
    private val userRepository: UserRepository
) : ProfileService {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing ProfileServiceImpl")
    }

    override suspend fun updateProfile(
        newUser: User,
        newEmail: String?,
        newPassword: String?
    ): Result<Unit> {
        logger.debug("Trying to update user profile: {}", newUser)

        return try {
            AuthValidator.validate(newUser)
            if (newPassword != null) {
                AuthValidator.validatePassword(newPassword)
            }

            userRepository.updateCredentials(newUser, newEmail, newPassword)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(id: String): Result<Unit> {
        logger.debug("Trying to delete user account with id {}", id)
        return try {
            userRepository.delete(id)
                ?: throw ValueNotFoundException("User not found.")
            userRepository.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }
}