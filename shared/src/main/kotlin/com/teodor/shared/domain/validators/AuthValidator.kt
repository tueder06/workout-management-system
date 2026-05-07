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

    fun validateEmail(email: String) {
        when {
            email.isBlank() ->
                throw ValidationException("Email is required.\n")
            !EMAIL_REGEX.matches(email) ->
                throw ValidationException("The email does not look like one.\n")
        }
    }

    fun validatePassword(password: String) {
        when {
            password.isBlank() ->
                throw ValidationException("Password is required.\n")
            password.length < 8 ->
                throw ValidationException("Password must contain at least 8 characters.\n")
        }
    }

    override fun validate(entity: User) {
        val errors = StringBuilder()

        if (entity.id != null && entity.id.isBlank()) {
            errors.append("Id cannot be empty.\n")
        }

        if (entity.username.trim().length < 2) {
            errors.append("Username must contain at least 2 characters.\n")
        }

        when {
            entity.email.isBlank() ->
                errors.append("Email is required.\n")
            !EMAIL_REGEX.matches(entity.email) ->
                errors.append("The email does not look like one.\n")
        }

        if (entity.firstName.trim().length < 2) {
            errors.append("First name must contain at least 2 characters.\n")
        }

        if (entity.lastName.trim().length < 2) {
            errors.append("Last name must contain at least 2 characters.\n")
        }

        if (!errors.isEmpty()) {
            throw ValidationException(errors.toString())
        }
    }
}