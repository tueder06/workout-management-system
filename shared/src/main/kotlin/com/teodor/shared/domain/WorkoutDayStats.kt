package com.teodor.shared.domain

import com.teodor.shared.domain.entities.PlannedExercise
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WorkoutDayStats(
    val id: Long? = null,
    val name: String,
    val exerciseCount: Int,
    val totalWeight: String,
    val duration: String,
    val lastSession: String
) {
    companion object {
        fun create(
            id: Long?,
            name: String,
            exercises: List<PlannedExercise>,
            lastSessionDate: Date? = null
        ): WorkoutDayStats {
            val count = exercises.size

            val calculatedWeight = exercises.sumOf { exercise ->
                if (exercise.targetWeight != null && exercise.targetReps != null) {
                    (exercise.targetWeight * exercise.targetReps * exercise.targetSets).toDouble()
                } else {
                    0.0
                }
            }

            val totalSeconds = exercises.sumOf { exercise ->
                val activeTime = if (exercise.targetWeight != null && exercise.targetReps != null) {
                    exercise.targetSets * exercise.targetReps * 5
                } else if (exercise.targetDurationSeconds != null) {
                    exercise.targetSets * exercise.targetDurationSeconds
                } else {
                    0
                }

                val pauses = (exercise.targetSets - 1).coerceAtLeast(0)
                val pauseTime = pauses * (exercise.pauseDurationSeconds)

                activeTime + pauseTime
            }

            val formattedWeight = if (calculatedWeight % 1.0 == 0.0) {
                "${calculatedWeight.toInt()} kg"
            } else {
                "${String.format(Locale.US, "%.1f", calculatedWeight)} kg"
            }

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60

            val formattedDuration = if (hours > 0) {
                "${hours}h:${minutes}min"
            } else {
                "${minutes}min"
            }

            val formattedDate = lastSessionDate?.let { date ->
                val formatter = SimpleDateFormat("dd MMM. yyyy", Locale.getDefault())
                formatter.format(date)
            } ?: "-"

            return WorkoutDayStats(
                id = id,
                name = name,
                exerciseCount = count,
                totalWeight = formattedWeight,
                duration = formattedDuration,
                lastSession = formattedDate
            )
        }
    }
}