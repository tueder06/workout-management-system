package com.teodor.shared.domain.validators

import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExerciseValidatorTest {
    @Test
    fun `validate returns success for valid exercises`() {
        val exercise1 = Exercise(
            id = 12,
            name = "Barbell Squat",
            mainMuscleGroup = MuscleGroup.UPPER_LEGS,
            secondaryMuscleGroups = listOf(MuscleGroup.LOWER_LEGS, MuscleGroup.GLUTES),
            equipmentUsed = listOf(EquipmentType.BARBELL, EquipmentType.BENCH),
            exerciseType = ExerciseType.REPETITION,
            userId = null
        )
        val result1 = ExerciseValidator.validate(exercise1)
        assertTrue { result1.isSuccess }

        val exercise2 = Exercise(
            id = 15,
            name = "Triceps Extension",
            mainMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = emptyList(),
            equipmentUsed = listOf(EquipmentType.CABLE_MACHINE),
            exerciseType = ExerciseType.REPETITION,
            userId = "1234"
        )
        val result2 = ExerciseValidator.validate(exercise2)
        assertTrue { result2.isSuccess }
    }

    @Test
    fun `validate returns failure for invalid exercises`() {
        val exercise1 = Exercise(
            id = -12,
            name = "",
            mainMuscleGroup = null,
            secondaryMuscleGroups = listOf(
                MuscleGroup.LOWER_LEGS,
                MuscleGroup.UPPER_LEGS,
                MuscleGroup.TRICEPS,
            ),
            equipmentUsed = listOf(
                EquipmentType.EZ_BAR,
                EquipmentType.BENCH,
                EquipmentType.DUMBBELL,
                EquipmentType.BODY_WEIGHT,
            ),
            exerciseType = null,
        )
        val result1 = ExerciseValidator.validate(exercise1)
        assertTrue { result1.isFailure }
        val errorMessage1 = result1.exceptionOrNull()
        assertTrue { errorMessage1?.message?.contains("Id") == true }
        assertTrue { errorMessage1?.message?.contains("Name") == true }
        assertTrue { errorMessage1?.message?.contains("Main muscle group") == true }
        assertTrue { errorMessage1?.message?.contains("secondary muscle groups") == true }
        assertTrue { errorMessage1?.message?.contains("equipments") == true }
        assertTrue { errorMessage1?.message?.contains("Exercise type") == true }

        val exercise2 = Exercise(
            id = 0,
            name = "Lateral Raise",
            mainMuscleGroup = MuscleGroup.SHOULDERS,
            secondaryMuscleGroups = listOf(
                MuscleGroup.LOWER_LEGS,
                MuscleGroup.SHOULDERS,
            ),
            exerciseType = ExerciseType.REPETITION,
        )
        val result2 = ExerciseValidator.validate(exercise2)
        assertTrue { result2.isFailure }
        assertTrue { result2.exceptionOrNull()?.message?.contains("Id") == true }
        assertTrue { result2.exceptionOrNull()?.message?.contains("Main muscle group cannot be secondary as well") == true }
    }
}