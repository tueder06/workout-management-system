package com.teodor.shared.service

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.entities.User
import com.teodor.shared.domain.validators.AuthValidator
import com.teodor.shared.persistence.UserRepository
import org.slf4j.LoggerFactory

class AuthServiceImpl(
    private val userRepository: UserRepository
) : AuthService {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing AuthServiceImpl")
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        logger.debug("Trying to login user with email {}", email)
        val errors = StringBuilder()
        AuthValidator.validateEmail(email).onFailure {
            errors.append(it.message)
        }
        AuthValidator.validatePassword(password).onFailure {
            errors.append(it.message)
        }
        if (errors.isNotEmpty()) {
            val errorMessage = errors.toString()
            logger.error("Validation failed: {}", errorMessage)
            return Result.failure(ValidationException(errorMessage))
        }

        return try {
            val user = userRepository.auth(email, password)
                ?: throw ValueNotFoundException("User not found.")

            Result.success(user)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun register(newUser: User, password: String): Result<Unit> {
        logger.debug("Trying to register user: {}", newUser)
        val errors = StringBuilder()
        AuthValidator.validate(newUser).onFailure {
            errors.append(it.message)
        }
        AuthValidator.validatePassword(password).onFailure {
            errors.append(it.message)
        }
        if (errors.isNotEmpty()) {
            val errorMessage = errors.toString()
            logger.error("Validation failed: {}", errorMessage)
            return Result.failure(ValidationException(errorMessage))
        }

        return try {
            userRepository.register(newUser, password)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        logger.debug("Trying to log out user")
        return try {
            userRepository.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        logger.debug("Trying to get currently logged user")
        return try {
            val loggedUser = userRepository.getCurrentUser()
                ?: throw ValueNotFoundException("User not found.")
            Result.success(loggedUser)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }
}