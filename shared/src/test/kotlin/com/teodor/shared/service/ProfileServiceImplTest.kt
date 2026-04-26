package com.teodor.shared.service

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

class ProfileServiceImplTest {
    private val userRepository = mockk<UserRepository>()
    private val profileService = ProfileServiceImpl(userRepository)
    private val validUser = User(
        id = "1",
        username = "teodor",
        email = "test@example.com",
        firstName = "Teodor",
        lastName = "Toader",
    )

    @Test
    fun `updateProfile succeeds changing credentials and public data`() = runTest {
        coEvery { userRepository.updateCredentials(any(), any(), any()) } returns Unit
        coEvery { userRepository.update(any()) } returns validUser

        val result = profileService.updateProfile(validUser, "new@email.com", "NewPass123!")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { userRepository.updateCredentials(validUser, "new@email.com", "NewPass123!") }
    }

    @Test
    fun `updateProfile fails if at least one new user detail is invalid`() = runTest {
        val invalidUser = User(
            id = "  \n",
            username = "b",
            email = "test@.bad",
            firstName = "a",
            lastName = "\rd",
        )
        val result = profileService.updateProfile(invalidUser, "new@email.com", "NewPass123!")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }

    @Test
    fun `updateProfile fails if new password is invalid`() = runTest {
        val result = profileService.updateProfile(validUser, "new@email.com", "123")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }

    @Test
    fun `updateProfile catches unexpected exceptions`() = runTest {
        coEvery { userRepository.update(any()) } throws Exception("Server Down")
        val result = profileService.updateProfile(validUser, null, null)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun `deleteAccount succeeds and logs out`() = runTest {
        coEvery { userRepository.delete(any()) } returns validUser
        coEvery { userRepository.logout() } returns Unit

        val result = profileService.deleteAccount("1")
        assertTrue(result.isSuccess)
        coVerify { userRepository.logout() }
    }

    @Test
    fun `deleteAccount fails if user not found`() = runTest {
        coEvery { userRepository.delete(any()) } returns null
        val result = profileService.deleteAccount("1")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
        coVerify(exactly = 0) { userRepository.logout() }
    }

    @Test
    fun `deleteAccount catches unexpected exceptions`() = runTest {
        coEvery { userRepository.delete(any()) } throws Exception("DB Error")
        val result = profileService.deleteAccount("1")
        assertTrue(result.isFailure)
    }
}