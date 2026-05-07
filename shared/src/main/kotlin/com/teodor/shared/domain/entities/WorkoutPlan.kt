package com.teodor.shared.domain.entities

import com.teodor.shared.domain.enums.TrainingGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutPlan(
    override val id: Long? = null,
    val name: String,
    val description: String,
    val goal: TrainingGoal?,

    @SerialName("user_id")
    val userId: String?,

    @SerialName("workout_days")
    val days: List<WorkoutDay> = emptyList()
) : Entity<Long> {
}