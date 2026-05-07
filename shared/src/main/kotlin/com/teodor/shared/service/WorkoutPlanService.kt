package com.teodor.shared.service

import com.teodor.shared.domain.entities.PlannedExercise
import com.teodor.shared.domain.entities.WorkoutDay
import com.teodor.shared.domain.entities.WorkoutPlan

interface WorkoutPlanService {
    suspend fun getPlanDetails(id: Long): Result<WorkoutPlan>
    suspend fun findWorkoutPlans(): Result<Iterable<WorkoutPlan>>
    suspend fun saveWorkoutPlan(workoutPlan: WorkoutPlan): Result<Unit>
    suspend fun updateWorkoutPlan(newWorkoutPlan: WorkoutPlan): Result<Unit>
    suspend fun deleteWorkoutPlan(id: Long): Result<WorkoutPlan>

    suspend fun getDayDetails(id: Long): Result<WorkoutDay>
    suspend fun saveWorkoutDay(workoutDay: WorkoutDay): Result<Unit>
    suspend fun updateWorkoutDay(newWorkoutDay: WorkoutDay): Result<Unit>
    suspend fun deleteWorkoutDay(id: Long): Result<WorkoutDay>

    suspend fun getExerciseDetails(id: Long): Result<PlannedExercise>
    suspend fun savePlannedExercise(plannedExercise: PlannedExercise): Result<Unit>
    suspend fun updatePlannedExercise(newPlannedExercise: PlannedExercise): Result<Unit>
    suspend fun deletePlannedExercise(id: Long): Result<PlannedExercise>
}