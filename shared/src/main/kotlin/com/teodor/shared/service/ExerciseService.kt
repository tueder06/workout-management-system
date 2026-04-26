package com.teodor.shared.service

import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.ExerciseFilter
import kotlinx.coroutines.flow.SharedFlow

interface ExerciseService {
    val updates: SharedFlow<Unit>
    suspend fun findExerciseById(id: Long): Result<Exercise>
    suspend fun saveCustomExercise(exercise: Exercise): Result<Unit>
    suspend fun updateCustomExercise(newExercise: Exercise): Result<Unit>
    suspend fun deleteCustomExercise(id: Long): Result<Exercise>
    suspend fun filterExercises(filter: ExerciseFilter): Result<Iterable<Exercise>>
}