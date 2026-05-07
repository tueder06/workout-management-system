package com.teodor.shared.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.WorkoutDayStats
import com.teodor.shared.domain.entities.WorkoutPlan
import com.teodor.shared.domain.enums.TrainingGoal
import com.teodor.shared.service.WorkoutPlanService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutPlanEditorViewModel(
    private val workoutPlanService: WorkoutPlanService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialPlanId = savedStateHandle.get<String>("planId")
    private val planId: Long? = initialPlanId?.toLongOrNull()

    private val _state = MutableStateFlow(WorkoutPlanEditorState())
    val state = _state.asStateFlow()

    init {
        if (planId != null) {
            loadPlan(planId)
        } else {
            enableEditMode()
        }
    }

    private fun loadPlan(planId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingInitialData = true, errorMessage = null) }

            workoutPlanService.getPlanDetails(planId)
                .onSuccess { plan ->
                    val statsDays = plan.days.map { day ->
                        WorkoutDayStats.create(
                            id = day.id,
                            name = day.name,
                            exercises = day.exercises,
                        )
                    }

                    _state.update {
                        it.copy(
                            planId = plan.id,
                            draftName = plan.name,
                            draftDescription = plan.description,
                            draftGoal = plan.goal,
                            draftDays = statsDays,
                            isEditing = false,
                            isLoadingInitialData = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = "Could not load plan: ${error.message}",
                            isLoadingInitialData = false
                        )
                    }
                }
        }
    }

    fun refreshDays() {
        val currentPlanId = _state.value.planId ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingDays = true, errorMessage = null) }

            workoutPlanService.getPlanDetails(currentPlanId)
                .onSuccess { plan ->
                    val statsDays = plan.days.map { day ->
                        WorkoutDayStats.create(
                            id = day.id,
                            name = day.name,
                            exercises = day.exercises,
                        )
                    }

                    _state.update {
                        it.copy(
                            draftDays = statsDays,
                            isLoadingDays = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = "Could not load plan days: ${error.message}",
                            isLoadingDays = false
                        )
                    }
                }
        }
    }

    fun enableEditMode() {
        _state.update { it.copy(isEditing = true) }
    }

    fun savePlan() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            val planToSave = WorkoutPlan(
                id = _state.value.planId,
                name = _state.value.draftName,
                description = _state.value.draftDescription,
                goal = _state.value.draftGoal,
                userId = null
            )

            val result = if (_state.value.planId == null) {
                workoutPlanService.saveWorkoutPlan(planToSave)
            } else {
                workoutPlanService.updateWorkoutPlan(planToSave)
            }

            result.onSuccess {
                _state.update { it.copy(isSaving = false, saveComplete = true, isEditing = false) }
            }.onFailure { error ->
                _state.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun updateName(newName: String) {
        _state.update { it.copy(draftName = newName) }
    }

    fun updateDescription(newDescription: String) {
        _state.update { it.copy(draftDescription = newDescription) }
    }

    fun updateGoal(newGoal: TrainingGoal) {
        _state.update { it.copy(draftGoal = newGoal) }
    }

    fun promptDeleteDay(dayId: Long?) {
        _state.update { it.copy(dayIdToDelete = dayId) }
    }

    fun dismissDeleteDayDialog() {
        _state.update { it.copy(dayIdToDelete = null) }
    }

    fun confirmDeleteDay() {
        val dayId = _state.value.dayIdToDelete ?: return
        viewModelScope.launch {
            workoutPlanService.deleteWorkoutDay(dayId)

            val updatedDays = _state.value.draftDays.filter { it.id != dayId }
            _state.update {
                it.copy(draftDays = updatedDays, dayIdToDelete = null)
            }
        }
    }
}