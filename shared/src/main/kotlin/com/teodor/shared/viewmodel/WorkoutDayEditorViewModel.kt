package com.teodor.shared.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.entities.WorkoutDay
import com.teodor.shared.service.WorkoutPlanService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutDayEditorViewModel(
    private val workoutDayService: WorkoutPlanService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialDayId = savedStateHandle.get<String>("dayId")
    private val dayId: Long? = initialDayId?.toLongOrNull()

    private val planIdString = savedStateHandle.get<String>("planId")
    private val planId: Long? = planIdString?.toLongOrNull()

    private val _state = MutableStateFlow(WorkoutDayEditorState(planId = planId))
    val state = _state.asStateFlow()

    init {
        if (dayId != null) {
            loadDay(dayId)
        } else {
            _state.update { it.copy(isEditing = true) }
        }
    }

    private fun loadDay(dayId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            workoutDayService.getDayDetails(dayId)
                .onSuccess { day ->
                    _state.update {
                        it.copy(
                            dayId = day.id,
                            draftName = day.name,
                            exercises = day.exercises,
                            isEditing = false,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(errorMessage = "Could not load day: ${error.message}", isLoading = false)
                    }
                }
        }
    }

    fun enableEditMode() {
        _state.update { it.copy(isEditing = true) }
    }

    fun updateName(newName: String) {
        _state.update { it.copy(draftName = newName) }
    }

    fun saveDay() {
        val isNewDay = _state.value.dayId == null

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            val dayToSave = WorkoutDay(
                id = _state.value.dayId,
                workoutPlanId = _state.value.planId ?: return@launch,
                name = _state.value.draftName
            )

            val result = if (isNewDay) {
                workoutDayService.saveWorkoutDay(dayToSave)
            } else {
                workoutDayService.updateWorkoutDay(dayToSave)
            }

            result.onSuccess {
                _state.update { it.copy(isSaving = false, saveComplete = isNewDay, isEditing = false) }
            }.onFailure { error ->
                _state.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun refreshExercises() {
        val currentDayId = _state.value.dayId ?: return

        viewModelScope.launch {
            workoutDayService.getDayDetails(currentDayId)
                .onSuccess { freshDay ->
                    _state.update { currentState ->
                        currentState.copy(exercises = freshDay.exercises)
                    }
                }
        }
    }

    fun promptDeleteExercise(exerciseId: Long?) {
        _state.update { it.copy(exerciseIdToDelete = exerciseId) }
    }

    fun dismissDeleteExerciseDialog() {
        _state.update { it.copy(exerciseIdToDelete = null) }
    }

    fun confirmDeleteExercise() {
        val exerciseId = _state.value.exerciseIdToDelete ?: return
        viewModelScope.launch {
            workoutDayService.deletePlannedExercise(exerciseId)

            val updatedExercises = _state.value.exercises.filter { it.id != exerciseId }
            _state.update {
                it.copy(exercises = updatedExercises, exerciseIdToDelete = null)
            }
        }
    }
}