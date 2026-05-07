package com.teodor.shared.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration

@Serializable
data class PlannedExercise(
    override val id: Long?,

    @SerialName("target_sets")
    val targetSets: Int,

    @SerialName("target_weight")
    val targetWeight: Float?,

    @SerialName("target_reps")
    val targetReps: Int?,

    @SerialName("target_duration")
    val targetDurationSeconds: Int?,

    @SerialName("pause_duration")
    val pauseDurationSeconds: Int,

    @SerialName("exercise_id")
    val exerciseId: Long?,

    @SerialName("workout_day_id")
    val workoutDayId: Long?,

    @SerialName("exercises")
    val exerciseDetails: Exercise? = null,
) : Entity<Long> {
    val targetDuration: Duration?
        get() = targetDurationSeconds?.let {
            Duration.ofSeconds(it.toLong())
        }

    val pauseDuration: Duration
        get() = Duration.ofSeconds(pauseDurationSeconds.toLong())

}