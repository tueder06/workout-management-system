package com.teodor.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teodor.shared.domain.ExerciseFilter
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import com.teodor.shared.service.ExerciseService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseLibraryViewModel(
    private val exerciseService: ExerciseService
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseLibraryState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadExercises()
        viewModelScope.launch {
            exerciseService.updates.collect {
                loadExercises()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        val cleanQuery = query.ifBlank { null }
        _state.update { it.copy(currentFilter = it.currentFilter.copy(nameSearchQuery = cleanQuery)) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            loadExercises()
        }
    }

    fun updateMuscleFilter(muscle: MuscleGroup?) {
        _state.update {
            it.copy(currentFilter = it.currentFilter.copy(muscleGroup = muscle))
        }
        loadExercises()
    }

    fun updateEquipmentFilter(equipment: EquipmentType?) {
        _state.update {
            it.copy(currentFilter = it.currentFilter.copy(equipmentType = equipment))
        }
        loadExercises()
    }

    fun updateTypeFilter(type: ExerciseType?) {
        _state.update {
            it.copy(currentFilter = it.currentFilter.copy(exerciseType = type))
        }
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            exerciseService.filterExercises(_state.value.currentFilter)
                .onSuccess { resultList ->
                    _state.update { it.copy(exercises = resultList.toList(), isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message, isLoading = false) }
                }
        }
    }
}