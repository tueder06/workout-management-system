package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.PlannedExercise

object PlannedExerciseValidator : Validator<PlannedExercise> {

    override fun validate(entity: PlannedExercise) {
        val errors = StringBuilder()

        if (entity.id != null && entity.id <= 0) {
            errors.append("Id must be a positive integer.\n")
        }

        if (entity.targetSets <= 0) {
            errors.append("Number of sets is required and must be positive.\n")
        }

        if (entity.targetWeight != null && entity.targetWeight < 0) {
            errors.append("Target weight must be a positive number.\n")
        }

        if (entity.targetReps != null && entity.targetReps <= 0) {
            errors.append("Target repetitions must be a positive integer.\n")
        }

        if ((entity.targetReps == null) != (entity.targetWeight == null)) {
            errors.append("Cannot have a target weight without a number of repetitions or the other way.\n")
        }

        if (entity.targetDurationSeconds != null && entity.targetDurationSeconds <= 0) {
            errors.append("Target duration must be a positive integer.\n")
        }

        if (entity.targetReps != null && entity.targetDuration != null) {
            errors.append("The planned exercise cannot be both of duration and repetition types.\n")
        }

        if (entity.targetReps == null && entity.targetWeight == null && entity.targetDuration == null) {
            errors.append("All exercise info are required.\n")
        }

        if (entity.pauseDurationSeconds <= 0) {
            errors.append("Pause duration must be a positive integer.\n")
        }

        if (entity.exerciseId == null || entity.exerciseId <= 0) {
            errors.append("The exercise to do is required.\n")
        }

        if (entity.workoutDayId == null || entity.workoutDayId <= 0) {
            errors.append("The workout day is required.\n")
        }

        if (!errors.isEmpty()) {
            throw ValidationException(errors.toString().trimEnd())
        }
    }
}