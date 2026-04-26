package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _state.update { it.copy(email = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _state.update { it.copy(password = newPassword) }
    }

    fun onLoginClicked() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = authService.login(
                email = _state.value.email,
                password = _state.value.password
            )

            result.onSuccess {
                _state.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetSuccessState() {
        _state.update { it.copy(isLoginSuccessful = false) }
    }
}