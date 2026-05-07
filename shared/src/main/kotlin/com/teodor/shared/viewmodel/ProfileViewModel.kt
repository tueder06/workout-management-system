package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.entities.User
import com.teodor.shared.service.AuthService
import com.teodor.shared.service.ProfileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onFailure

class ProfileViewModel(
    private val authService: AuthService,
    private val profileService: ProfileService
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var cachedUserId: String? = null

    init { loadUser() }

    private fun loadUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = authService.getCurrentUser()

            result.onSuccess { domainUser ->
                cachedUserId = domainUser.id
                _state.update {
                    it.copy(
                        username = domainUser.username,
                        email = domainUser.email,
                        firstName = domainUser.firstName,
                        lastName = domainUser.lastName,

                        draftUsername = domainUser.username,
                        draftEmail = domainUser.email,
                        draftFirstName = domainUser.firstName,
                        draftLastName = domainUser.lastName,

                        isLoading = false
                    )
                }
            }.onFailure { _ ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load profile data. Your session may have expired."
                    )
                }
            }
        }
    }

    fun toggleEditMode(field: String) {
        _state.update {
            val currentFields = it.editingFields.toMutableSet()
            if (currentFields.contains(field)) currentFields.remove(field) else currentFields.add(field)
            it.copy(editingFields = currentFields)
        }
    }

    fun onDraftChange(field: String, value: String) {
        _state.update {
            when (field) {
                "username" -> it.copy(draftUsername = value)
                "email" -> it.copy(draftEmail = value)
                "firstName" -> it.copy(draftFirstName = value)
                "lastName" -> it.copy(draftLastName = value)
                "password" -> it.copy(draftPassword = value)
                "confirmPassword" -> it.copy(draftConfirmPassword = value)
                else -> it
            }
        }
    }

    fun onSaveClicked() = _state.update { it.copy(showSaveDialog = true) }
    fun onLogoutClicked() = _state.update { it.copy(showLogoutDialog = true) }
    fun onDeleteClicked() = _state.update { it.copy(showDeleteDialog = true) }
    fun dismissDialogs() = _state.update {
        it.copy(showSaveDialog = false, showLogoutDialog = false, showDeleteDialog = false)
    }

    fun confirmSave() {
        val currentState = _state.value
        if (currentState.draftPassword.isNotEmpty() && currentState.draftPassword != currentState.draftConfirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match.", showSaveDialog = false) }
            return
        }

        val currentId = cachedUserId
        if (currentId == null) {
            _state.update { it.copy(errorMessage = "Session error. Please log in again.", showSaveDialog = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, errorMessage = null) }
            val newEmail = if (currentState.draftEmail != currentState.email) currentState.draftEmail else null
            val newPassword = currentState.draftPassword.ifEmpty { null }
            val newUser = User(
                id = cachedUserId,
                email = newEmail ?: _state.value.email,
                username = _state.value.draftUsername,
                firstName = _state.value.draftFirstName,
                lastName = _state.value.draftLastName
            )

            val result = profileService.updateProfile(newUser, newEmail, newPassword)
            result.onSuccess {
                _state.update {
                    it.copy(
                        username = it.draftUsername,
                        email = it.draftEmail,
                        firstName = it.draftFirstName,
                        lastName = it.draftLastName,
                        draftPassword = "",
                        draftConfirmPassword = "",
                        editingFields = emptySet(),
                        showSaveDialog = false,
                        errorMessage = null,
                        isActionLoading = false
                ) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isActionLoading = false,
                        showSaveDialog = false,
                        errorMessage = error.message,
                        draftUsername = it.username,
                        draftEmail = it.email,
                        draftFirstName = it.firstName,
                        draftLastName = it.lastName,
                        draftPassword = "",
                        draftConfirmPassword = "",
                        editingFields = emptySet()
                    )
                }
            }
        }
    }

    fun confirmLogout() {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, errorMessage = null) }
            val result = authService.logout()
            result.onSuccess {
                _state.update { it.copy(showLogoutDialog = false, isActionLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isActionLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun confirmDelete() {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true, errorMessage = null) }
            val result = cachedUserId?.let { profileService.deleteAccount(it) }
            result?.onSuccess {
                _state.update { it.copy(showDeleteDialog = false, isActionLoading = false) }
            }?.onFailure { error ->
                _state.update { it.copy(isActionLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}