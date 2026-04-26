package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.Exercise

object ExerciseValidator : Validator<Exercise> {
    override fun validate(entity: Exercise): Result<Unit> {
        val errors = StringBuilder()

        if (entity.id != null && entity.id <= 0) {
            errors.append("Id must be a positive integer.\n")
        }

        if (entity.name.isBlank()) {
            errors.append("Name is required.\n")
        }

        if (entity.mainMuscleGroup == null) {
            errors.append("Main muscle group is required.\n")
        }

        if (entity.secondaryMuscleGroups.size > 2) {
            errors.append("No more than 2 secondary muscle groups can be chosen.")
        }

        if (entity.mainMuscleGroup != null &&
            entity.secondaryMuscleGroups.contains(entity.mainMuscleGroup)) {
            errors.append("Main muscle group cannot be secondary as well.\n")
        }

        if (entity.equipmentUsed.size > 2) {
            errors.append("No more than 2 equipments can be chosen.")
        }

        if (entity.exerciseType == null) {
            errors.append("Exercise type is required.\n")
        }

        return if (errors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(errors.toString()))
        }
    }
}