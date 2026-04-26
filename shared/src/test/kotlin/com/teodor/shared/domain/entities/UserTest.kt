package com.teodor.shared.domain.entities

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserTest {
    private val jsonConfig = Json { ignoreUnknownKeys = true }

    @Test
    fun `User entity serializes and deserializes correctly`() {
        val originalUser = User(
            id = "uuid-1234-5678",
            username = "DanDev",
            email = "test@forma.com",
            firstName = "Dan",
            lastName = "Toader",
        )

        val jsonString = jsonConfig.encodeToString(originalUser)
        val decodedUser = jsonConfig.decodeFromString<User>(jsonString)

        assertEquals(originalUser.id, decodedUser.id)
        assertEquals(originalUser.username, decodedUser.username)
        assertEquals(originalUser.email, decodedUser.email)
        assertEquals(originalUser.firstName, decodedUser.firstName)
        assertEquals(originalUser.lastName, decodedUser.lastName)
    }
}