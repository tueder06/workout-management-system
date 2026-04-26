package com.teodor.shared.service

import com.teodor.shared.domain.DuplicateValueException
import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.entities.User
import com.teodor.shared.persistence.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AuthServiceImplTest {
    private val userRepository = mockk<UserRepository>()
    private val authService = AuthServiceImpl(userRepository)

    private val validUser = User(
        id = "1",
        username = "teodor",
        email = "test@example.com",
        firstName = "Teodor",
        lastName = "Toader",
    )
    private val validPass = "StrongPass123!"

    @Test
    fun `login succeeds with valid credentials`() = runTest {
        coEvery { userRepository.auth(any(), any()) } returns validUser
        val result = authService.login(validUser.email, validPass)
        assertTrue(result.isSuccess)
        assertEquals(validUser, result.getOrNull())
    }

    @Test
    fun `login accumulates multiple validation errors`() = runTest {
        val result = authService.login("bademail", "123")
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ValidationException

        assertTrue(exception.message?.contains("email", ignoreCase = true) == true &&
                exception.message?.contains("password", ignoreCase = true) == true)
        coVerify(exactly = 0) { userRepository.auth(any(), any()) }
    }

    @Test
    fun `login returns ValueNotFoundException when repo returns null`() = runTest {
        coEvery { userRepository.auth(any(), any()) } returns null
        val result = authService.login(validUser.email, validPass)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
    }

    @Test
    fun `login safely catches unexpected repository exceptions`() = runTest {
        coEvery { userRepository.auth(any(), any()) } throws RuntimeException("Network Timeout")
        val result = authService.login(validUser.email, validPass)
        assertTrue(result.isFailure)
        assertEquals("Network Timeout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register succeeds and returns User`() = runTest {
        coEvery { userRepository.register(any(), any()) } returns validUser
        val result = authService.register(validUser, validPass)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `register fails on validation before hitting database`() = runTest {
        val badUser = validUser.copy(email = "invalid")
        val result = authService.register(badUser, "123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
        coVerify(exactly = 0) { userRepository.register(any(), any()) }
    }

    @Test
    fun `register with an already used email`() = runTest {
        coEvery { userRepository.register(any(), any()) } throws DuplicateValueException("Email already used")
        val result = authService.register(validUser, validPass)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DuplicateValueException)
    }

    @Test
    fun `register safely catches unexpected repository exceptions`() = runTest {
        coEvery { userRepository.register(any(), any()) } throws IllegalStateException("Database Locked")
        val result = authService.register(validUser, validPass)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `logout succeeds`() = runTest {
        coEvery { userRepository.logout() } returns Unit
        val result = authService.logout()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `logout safely catches unexpected repository exceptions`() = runTest {
        coEvery { userRepository.logout() } throws Exception("Already logged out")
        val result = authService.logout()
        assertTrue(result.isFailure)
    }

    @Test
    suspend fun `getCurrentUser should return a user when user is found`() {
        coEvery { userRepository.getCurrentUser() } returns validUser
        val result = authService.getCurrentUser()
        assertTrue(result.isSuccess)
        assertEquals(validUser, result.getOrNull())
    }

    @Test
    suspend fun `getCurrentUser should return failure when user is not found`() {
        coEvery { userRepository.getCurrentUser() } returns null
        val result = authService.getCurrentUser()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
    }

    @Test
    suspend fun `getCurrentUser should return failure when an exception occurs`() {
        val exception = RuntimeException("Unexpected error")
        coEvery { userRepository.getCurrentUser() } throws exception
        val result = authService.getCurrentUser()
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}