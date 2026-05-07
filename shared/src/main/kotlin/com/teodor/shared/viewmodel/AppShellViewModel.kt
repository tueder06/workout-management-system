package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppShellViewModel(
    supabase: SupabaseClient
) : ViewModel() {
    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                println("DEBUG_AUTH: The current status is $status")
            }
        }
    }

    val authState: StateFlow<AppAuthState> = supabase.auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Initializing -> AppAuthState.Loading
                is SessionStatus.Authenticated,
                 -> AppAuthState.Authenticated
                else -> AppAuthState.Unauthenticated
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppAuthState.Loading
        )
}