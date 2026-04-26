package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.entities.User
import com.teodor.shared.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEmailChanged(email: String) = _state.update {
        it.copy(email = email)
    }

    fun onUsernameChanged(username: String) = _state.update {
        it.copy(username = username)
    }

    fun onFirstNameChanged(name: String) = _state.update {
        it.copy(firstName = name)
    }

    fun onLastNameChanged(name: String) = _state.update {
        it.copy(lastName = name)
    }

    fun onPasswordChanged(password: String) = _state.update {
        it.copy(password = password)
    }

    fun onConfirmPasswordChanged(password: String) = _state.update {
        it.copy(confirmPassword = password)
    }

    fun onRegisterClicked() {
        val currentState = _state.value

        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        if (currentState.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val newUser = User(
                username = currentState.username,
                email = currentState.email,
                firstName = currentState.firstName,
                lastName = currentState.lastName
            )
            val result = authService.register(
                newUser = newUser,
                password = currentState.password
            )

            result.onSuccess {
                _state.update { it.copy(isLoading = false, isRegisterSuccessful = true) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetSuccessState() = _state.update {
        it.copy(isRegisterSuccessful = false)
    }
}