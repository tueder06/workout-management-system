package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthValidatorTest {
    @Test
    fun `validateEmail does not throw exception for valid emails`() {
        assertDoesNotThrow {
            AuthValidator.validateEmail("test@example.com")
        }
    }

    @Test
    fun `validateEmail throws ValidationException for blank email`() {
        val exception1 = assertThrows<ValidationException> {
            AuthValidator.validateEmail("")
        }
        assertEquals("Email is required.\n", exception1.message)

        val exception2 = assertThrows<ValidationException> {
            AuthValidator.validateEmail("    \n \t")
        }
        assertEquals("Email is required.\n", exception2.message)
    }

    @Test
    fun `validateEmail returns failure for missing domain`() {
        val exception = assertThrows<ValidationException> {
            AuthValidator.validateEmail("test@.com")
        }
        assertEquals("The email does not look like one.\n", exception.message)
    }

    @Test
    fun `validatePassword returns failure for short passwords`() {
        val exception1 = assertThrows<ValidationException> {
            AuthValidator.validatePassword("12345")
        }
        assertTrue(exception1.message?.contains("at least 8 characters") == true)

        val exception2 = assertThrows<ValidationException> {
            AuthValidator.validatePassword("   \n")
        }
        assertTrue(exception2.message?.contains("Password is required") == true)
    }

    @Test
    fun `validatePassword returns success for valid passwords`() {
        assertDoesNotThrow {
            AuthValidator.validatePassword("StrongPass123!")
        }
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
        assertDoesNotThrow {
            AuthValidator.validate(user)
        }
    }

    @Test
    fun `validate returns failure for users not respecting various constraints`() {
        val user1 = User(
            id = "  \r",
            username = "\n a    ",
            email = "test@ex",
            firstName = " H \t\n ",
            lastName = ""
        )
        val exception1 = assertThrows<ValidationException> {
            AuthValidator.validate(user1)
        }
        val errorMessage1 = exception1.message
        assertTrue(errorMessage1?.contains("Id") == true)
        assertTrue(errorMessage1?.contains("Username") == true)
        assertTrue(errorMessage1?.contains("email") == true)
        assertTrue(errorMessage1?.contains("First name") == true)
        assertTrue(errorMessage1?.contains("Last name") == true)

        val user2 = User(
            id = "12345",
            username = "",
            email = "\r ",
            firstName = "Dorian",
            lastName = "Hope"
        )
        val exception2 = assertThrows<ValidationException> {
            AuthValidator.validate(user2)
        }
        val errorMessage2 = exception2.message
        assertTrue(errorMessage2?.contains("Username") == true)
        assertTrue(errorMessage2?.contains("Email") == true)
    }
}