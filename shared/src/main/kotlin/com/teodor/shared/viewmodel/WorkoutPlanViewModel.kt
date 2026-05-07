package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.service.WorkoutPlanService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutPlanViewModel(
    private val workoutPlanService: WorkoutPlanService
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutPlanState())
    val state: StateFlow<WorkoutPlanState> = _state.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = workoutPlanService.findWorkoutPlans()

            result.onSuccess { fetchedPlans ->
                _state.update {
                    it.copy(isLoading = false, workoutPlans = fetchedPlans.toList())
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }
        }
    }

    fun promptDelete(planId: Long?) {
        _state.update { it.copy(planToDelete = planId) }
    }

    fun dismissDeleteDialog() {
        _state.update { it.copy(planToDelete = null) }
    }

    fun confirmDelete() {
        val planId = _state.value.planToDelete ?: return

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, errorMessage = null) }

            val result = workoutPlanService.deleteWorkoutPlan(planId)

            result.onSuccess {
                val updatedPlans = _state.value.workoutPlans.filter { it.id != planId }

                _state.update {
                    it.copy(isDeleting = false, workoutPlans = updatedPlans, planToDelete = null)
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(isDeleting = false, errorMessage = error.message, planToDelete = null)
                }
            }
        }
    }
}