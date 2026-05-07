package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.WorkoutPlan

object WorkoutPlanValidator : Validator<WorkoutPlan> {

    override fun validate(entity: WorkoutPlan) {
        val errors = StringBuilder()

        if (entity.id != null && entity.id <= 0) {
            errors.append("Id must be a positive integer.\n")
        }

        if (entity.name.isBlank()) {
            errors.append("Name is required.\n")
        }

        if (entity.description.isBlank()) {
            errors.append("Description is required.\n")
        }

        if (entity.goal == null) {
            errors.append("Training goal is required.\n")
        }

        if (entity.userId != null && entity.userId.isBlank()) {
            errors.append("User is required.\n")
        }

        if (!errors.isEmpty()) {
            throw ValidationException(errors.toString().trimEnd())
        }
    }
}