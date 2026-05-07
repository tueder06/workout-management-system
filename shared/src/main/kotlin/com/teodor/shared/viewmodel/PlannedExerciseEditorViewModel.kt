package com.teodor.shared.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.ExerciseFilter
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.entities.PlannedExercise
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.service.ExerciseService
import com.teodor.shared.service.WorkoutPlanService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onFailure

class PlannedExerciseEditorViewModel(
    private val plannedExerciseService: WorkoutPlanService,
    private val exerciseService: ExerciseService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialExerciseId = savedStateHandle.get<String>("exerciseId")
    private val exerciseId: Long? = initialExerciseId?.takeIf { it != "null" }?.toLongOrNull()

    private val dayIdString = savedStateHandle.get<String>("dayId")
    private val dayId: Long? = dayIdString?.takeIf { it != "null" }?.toLongOrNull()

    private val _state = MutableStateFlow(PlannedExerciseEditorState(dayId = dayId))
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val fetchExercises = exerciseService.filterExercises(ExerciseFilter())
            fetchExercises.onSuccess { masterExercises ->
                _state.update { it.copy(availableExercises = masterExercises.toList()) }
            }

            if (exerciseId != null) {
                plannedExerciseService.getExerciseDetails(exerciseId)
                    .onSuccess { plannedEx ->
                        _state.update {
                            it.copy(
                                plannedExerciseId = plannedEx.id,
                                dayId = plannedEx.workoutDayId,
                                selectedExercise = plannedEx.exerciseDetails,
                                targetSets = plannedEx.targetSets.toString(),
                                targetReps = plannedEx.targetReps?.toString() ?: "",
                                targetWeight = plannedEx.targetWeight?.toString() ?: "",
                                targetDuration = plannedEx.targetDurationSeconds?.toString() ?: "",
                                pauseDurationSeconds = plannedEx.pauseDurationSeconds.toString(),
                                isEditing = false,
                                isLoading = false
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(errorMessage = error.message, isLoading = false) }
                    }
            } else {
                _state.update { it.copy(isEditing = true, isLoading = false) }
            }
        }
    }

    fun enableEditMode() {
        _state.update { it.copy(isEditing = true) }
    }

    fun updateExercise(exercise: Exercise) {
        _state.update { it.copy(selectedExercise = exercise) }
    }

    fun updateSets(sets: String) {
        _state.update { it.copy(targetSets = sets) }
    }

    fun updateReps(reps: String) {
        _state.update { it.copy(targetReps = reps) }
    }

    fun updateWeight(weight: String) {
        _state.update { it.copy(targetWeight = weight) }
    }

    fun updateDuration(duration: String) {
        _state.update { it.copy(targetDuration = duration) }
    }

    fun updatePause(pause: String) {
        _state.update { it.copy(pauseDurationSeconds = pause) }
    }

    fun saveExercise() {
        val currentState = _state.value
        val parentDayId = currentState.dayId ?: return
        val baseExerciseId = currentState.selectedExercise?.id ?: run {
            _state.update { it.copy(errorMessage = "Please select an exercise.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val isDuration = currentState.selectedExercise.exerciseType == ExerciseType.DURATION

                val entityToSave = PlannedExercise(
                    id = currentState.plannedExerciseId,
                    workoutDayId = parentDayId,
                    exerciseId = baseExerciseId,
                    targetSets = currentState.targetSets.toIntOrNull() ?: 0,
                    targetDurationSeconds = if (isDuration) currentState.targetDuration.toIntOrNull() else null,
                    targetReps = if (!isDuration) currentState.targetReps.toIntOrNull() else null,
                    targetWeight = if (!isDuration) currentState.targetWeight.toFloatOrNull() else null,
                    pauseDurationSeconds = currentState.pauseDurationSeconds.toInt()
                )

                val result = if (currentState.plannedExerciseId == null) {
                    plannedExerciseService.savePlannedExercise(entityToSave)
                } else {
                    plannedExerciseService.updatePlannedExercise(entityToSave)
                }

                result.onSuccess {
                    _state.update { it.copy(isSaving = false, saveComplete = true) }
                }.onFailure { error ->
                    _state.update { it.copy(isSaving = false, errorMessage = error.message) }
                }
            } catch (_: NumberFormatException) {
                _state.update { it.copy(isSaving = false, errorMessage = "All exercise info are required.") }
            }
        }
    }
}