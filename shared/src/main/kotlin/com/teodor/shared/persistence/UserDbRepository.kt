package com.teodor.shared.persistence

import com.teodor.shared.domain.DuplicateValueException
import com.teodor.shared.domain.InvalidCredentialsException
import com.teodor.shared.domain.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.slf4j.LoggerFactory

class UserDbRepository(
    private val supabase: SupabaseClient
) : UserRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing UserDbRepository")
    }

    private val table = supabase.from("profiles")

    override suspend fun save(entity: User): User {
        logger.debug("Saving user: {}", entity)
        val savedUser = table.insert(entity) {
            select()
        }.decodeSingle<User>()
        logger.debug("User stored successfully: {}", savedUser)
        return savedUser
    }

    override suspend fun findById(id: String): User? {
        logger.debug("Finding user with id {}", id)
        val foundUser = table.select {
            filter { eq("id", id) }
        }.decodeSingleOrNull<User>()
        logger.debug("User found: {}", foundUser ?: "NOT FOUND")
        return foundUser
    }

    override suspend fun update(entity: User): User? {
        logger.debug("Updating user with new values: {}", entity)
        val userId = entity.id ?: return null
        val updatedUser = table.update(entity) {
            filter { eq("id", userId) }
            select()
        }.decodeSingleOrNull<User>()
        logger.debug("The updated user is: {}", updatedUser ?: "NOT FOUND")
        return updatedUser
    }

    override suspend fun delete(id: String): User? {
        logger.debug("Deleting user with id {}", id)
        val userToDelete = findById(id) ?: return null
        supabase.postgrest.rpc("delete_user")
        logger.debug("Deleted user: {}", userToDelete)
        return userToDelete
    }

    override suspend fun auth(
        email: String,
        password: String
    ): User? {
        logger.debug("Authenticating user with email {}", email)
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val authUser = supabase.auth.currentUserOrNull() ?: return null
            val mappedUser = mapAuthUser(authUser)
            logger.debug("User has been authenticated: {}", mappedUser)
            mappedUser
        } catch (e: AuthRestException) {
            val errorType = e.error
            throw when {
                errorType.contains("invalid_credentials")
                    -> InvalidCredentialsException("Invalid email or password.")
                else -> e
            }
        }
    }

    override suspend fun register(
        user: User,
        password: String
    ): User {
        logger.debug("Registering user: {}", user)
        if (!isUsernameAvailable(user.username)) {
            throw DuplicateValueException("Username already taken.")
        }
        try {
            val authUser = supabase.auth.signUpWith(Email) {
                this.email = user.email
                this.password = password
                this.data = buildJsonObject {
                    put("username", user.username)
                    put("first_name", user.firstName)
                    put("last_name", user.lastName)
                }
            }

            val id = authUser?.id
            val userToSave = user.copy(id = id)
            return userToSave
        }
        catch (e: AuthRestException) {
            when (e.error) {
                "user_already_exists" -> {
                    throw DuplicateValueException("Email already linked to an account.")
                }
                else -> throw e
            }
        }
    }

    override suspend fun logout() {
        logger.debug("Logging out user")
        supabase.auth.signOut()
        logger.debug("User logged out")
    }

    override suspend fun updateCredentials(newUser: User, newEmail: String?, newPassword: String?) {
        logger.debug("Updating the details for the user with email {} to: {}", newEmail, newUser)
        if (!isUsernameAvailable(newUser.username)) {
            throw DuplicateValueException("Username already taken.")
        }
        val metadataPayload = buildJsonObject {
            put("username", newUser.username)
            put("first_name", newUser.firstName)
            put("last_name", newUser.lastName)
        }
        try {
            supabase.auth.updateUser {
                if (!newEmail.isNullOrBlank()) email = newEmail
                if (!newPassword.isNullOrBlank()) password = newPassword
                data = metadataPayload
            }
        } catch (e: AuthRestException) {
            when (e.error) {
                "same_password" -> {
                    logger.warn("The same password has been typed. Retry but without changing the password")
                    supabase.auth.updateUser {
                        if (!newEmail.isNullOrBlank()) email = newEmail
                        data = metadataPayload
                    }
                }
                "email_exists" -> {
                    throw DuplicateValueException("Email already linked to an account.")
                }
                else -> throw e
            }
        }
        logger.debug("User details updated successfully")
    }

    override suspend fun getCurrentUser(): User? {
        logger.debug("Getting currently logged in user")
        val authUser = supabase.auth.currentUserOrNull() ?: return null
        val loggedUser = mapAuthUser(authUser)
        logger.debug("Found logged user: {}", loggedUser)
        return loggedUser
    }

    private fun mapAuthUser(authUser: UserInfo): User {
        val metadata = authUser.userMetadata
        val firstName = metadata?.get("first_name")?.jsonPrimitive?.content ?: ""
        val lastName = metadata?.get("last_name")?.jsonPrimitive?.content ?: ""
        val username = metadata?.get("username")?.jsonPrimitive?.content ?: ""

        return User(
            id = authUser.id,
            email = authUser.email ?: "",
            firstName = firstName,
            lastName = lastName,
            username = username
        )
    }

    private suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            logger.debug("Searching to see if the username {} is taken", username)
            val currentUserId = supabase.auth.currentUserOrNull()?.id
            val isAvailable = supabase.postgrest.rpc(
                function = "check_username_available",
                parameters = buildJsonObject {
                    put("target_username", username)
                    put("exclude_id", currentUserId)
                }
            ).decodeAs<Boolean>()
            logger.debug("Username available: {}", isAvailable)
            isAvailable
        } catch (e: Exception) {
            logger.error("Error occurred:", e)
            false
        }
    }
}