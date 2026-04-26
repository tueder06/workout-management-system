package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import com.teodor.shared.service.ExerciseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseEditorViewModel(
    private val exerciseService: ExerciseService
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseEditorState())
    val state = _state.asStateFlow()

    fun loadExercise(exerciseId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingInitialData = true, errorMessage = null) }
            exerciseService.findExerciseById(exerciseId)
                .onSuccess { exercise ->
                    _state.update {
                        it.copy(
                            exerciseId = exercise.id,
                            draftName = exercise.name,
                            draftMainMuscle = exercise.mainMuscleGroup,
                            draftSecondaryMuscles = exercise.secondaryMuscleGroups,
                            draftEquipment = exercise.equipmentUsed,
                            draftExerciseType = exercise.exerciseType,
                            isEditing = false,
                            isCustom = exercise.isCustom,
                            isLoadingInitialData = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = "Could not load exercise: ${error.message}",
                            isLoadingInitialData = false
                        )
                    }
                }
        }
    }

    fun startNewExercise() {
        _state.update { ExerciseEditorState(isEditing = true) }
    }

    fun enableEditMode() {
        _state.update { it.copy(isEditing = true) }
    }

    fun saveExercise() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }

            val exerciseToSave = Exercise(
                id = _state.value.exerciseId,
                name = _state.value.draftName,
                mainMuscleGroup = _state.value.draftMainMuscle,
                secondaryMuscleGroups = _state.value.draftSecondaryMuscles,
                equipmentUsed = _state.value.draftEquipment,
                exerciseType = _state.value.draftExerciseType,
            )

            val result = if (_state.value.exerciseId == null) {
                exerciseService.saveCustomExercise(exerciseToSave)
            } else {
                exerciseService.updateCustomExercise(exerciseToSave)
            }

            result.onSuccess {
                _state.update { it.copy(isSaving = false, saveComplete = true, isEditing = false) }
            }.onFailure { error ->
                _state.update { it.copy(isSaving = false, errorMessage = error.message) }
            }
        }
    }

    fun toggleDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = !it.showDeleteDialog) }
    }

    fun deleteExercise() {
        val id = _state.value.exerciseId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }

            exerciseService.deleteCustomExercise(id).onSuccess {
                _state.update { it.copy(saveComplete = true) }
            }.onFailure { error ->
                _state.update { it.copy(isDeleting = false, errorMessage = error.message) }
            }
        }
    }

    fun updateName(newName: String) {
        _state.update { it.copy(draftName = newName) }
    }

    fun updateMainMuscle(muscle: MuscleGroup) {
        _state.update { it.copy(draftMainMuscle = muscle) }
    }

    fun setExerciseType(type: ExerciseType) {
        _state.update { it.copy(draftExerciseType = type) }
    }

    fun toggleSecondaryMuscle(muscle: MuscleGroup) {
        _state.update { currentState ->
            val currentList = currentState.draftSecondaryMuscles
            val newList = if (currentList.contains(muscle)) {
                currentList - muscle
            } else {
                currentList + muscle
            }
            currentState.copy(draftSecondaryMuscles = newList)
        }
    }

    fun toggleEquipment(equipment: EquipmentType) {
        _state.update { currentState ->
            val currentList = currentState.draftEquipment
            val newList = if (currentList.contains(equipment)) {
                currentList - equipment
            } else {
                currentList + equipment
            }
            currentState.copy(draftEquipment = newList)
        }
    }
}