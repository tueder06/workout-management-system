package com.teodor.shared.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDay(
    override val id: Long?,
    val name: String,

    @SerialName("workout_plan_id")
    val workoutPlanId: Long?,

    @SerialName("planned_exercises")
    val exercises: List<PlannedExercise> = emptyList()
) : Entity<Long> {
}