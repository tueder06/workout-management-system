package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.WorkoutDay

object WorkoutDayValidator : Validator<WorkoutDay> {
    override fun validate(entity: WorkoutDay) {
        val errors = StringBuilder()

        if (entity.id != null && entity.id <= 0) {
            errors.append("Id must be a positive integer.\n")
        }

        if (entity.name.isBlank()) {
            errors.append("Name is required.\n")
        }

        if (entity.workoutPlanId == null || entity.workoutPlanId <= 0) {
            errors.append("Workout day is required.\n")
        }

        if (!errors.isEmpty()) {
            throw ValidationException(errors.toString().trimEnd())
        }
    }
}