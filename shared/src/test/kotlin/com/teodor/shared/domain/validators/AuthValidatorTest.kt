package com.teodor.shared.domain.validators

import com.teodor.shared.domain.entities.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthValidatorTest {
    @Test
    fun `validateEmail returns success for valid emails`() {
        val result = AuthValidator.validateEmail("test@example.com")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validateEmail returns failure for blank email`() {
        val result1 = AuthValidator.validateEmail("")
        assertTrue(result1.isFailure)
        assertTrue(result1.exceptionOrNull()?.message?.contains("Email is required") == true)
        val result2 = AuthValidator.validateEmail("  \n")
        assertTrue(result2.isFailure)
        assertTrue(result2.exceptionOrNull()?.message?.contains("Email is required") == true)
    }

    @Test
    fun `validateEmail returns failure for missing domain`() {
        val result = AuthValidator.validateEmail("test@.com")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("does not look like one") == true)
    }

    @Test
    fun `validatePassword returns failure for short passwords`() {
        val result1 = AuthValidator.validatePassword("12345")
        assertTrue(result1.isFailure)
        assertTrue(result1.exceptionOrNull()?.message?.contains("at least 8 characters") == true)
        val result2 = AuthValidator.validatePassword(" \n\r")
        assertTrue(result2.isFailure)
        assertTrue(result2.exceptionOrNull()?.message?.contains("Password is required") == true)
    }

    @Test
    fun `validatePassword returns success for valid passwords`() {
        val result = AuthValidator.validatePassword("StrongPass123!")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validate returns success for valid user`() {
        val user = User(
            id = "1234",
            username = "GainsMaxing",
            email = "gainer@maxxer.com",
            firstName = "Dany",
            lastName = "Bobber"
        )
        val result = AuthValidator.validate(user)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validate returns failure for users not respecting various constraints`() {
        val user = User(
            id = "  \r",
            username = "\n a    ",
            email = "test@ex",
            firstName = " H \t\n ",
            lastName = ""
        )
        val result = AuthValidator.validate(user)
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message
        assertTrue(errorMessage?.contains("Id") == true)
        assertTrue(errorMessage?.contains("Username") == true)
        assertTrue(errorMessage?.contains("email") == true)
        assertTrue(errorMessage?.contains("First name") == true)
        assertTrue(errorMessage?.contains("Last name") == true)
    }
}