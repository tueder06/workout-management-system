package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.User

object AuthValidator : Validator<User> {
    private val EMAIL_REGEX = Regex(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,9})$"
    )

    fun validateEmail(email: String): Result<Unit> {
        return when {
            email.isBlank() ->
                Result.failure(ValidationException("Email is required.\n"))
            !EMAIL_REGEX.matches(email) ->
                Result.failure(ValidationException("The email does not look like one.\n"))
            else -> Result.success(Unit)
        }
    }

    fun validatePassword(password: String): Result<Unit> {
        return when {
            password.isBlank() ->
                Result.failure(ValidationException("Password is required.\n"))
            password.length < 8 ->
                Result.failure(ValidationException("Password must contain at least 8 characters.\n"))
            else -> Result.success(Unit)
        }
    }

    override fun validate(entity: User): Result<Unit> {
        val errors = StringBuilder()

        if (entity.id != null && entity.id.isBlank()) {
            errors.append("Id cannot be empty.\n")
        }

        if (entity.username.trim().length < 2) {
            errors.append("Username must contain at least 2 characters.\n")
        }

        val checkEmail = validateEmail(entity.email)
        if (checkEmail.isFailure) {
            val message = checkEmail.exceptionOrNull()?.message
            errors.append(message)
        }

        if (entity.firstName.trim().length < 2) {
            errors.append("First name must contain at least 2 characters.\n")
        }

        if (entity.lastName.trim().length < 2) {
            errors.append("Last name must contain at least 2 characters.\n")
        }

        return if (errors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(errors.toString()))
        }
    }
}