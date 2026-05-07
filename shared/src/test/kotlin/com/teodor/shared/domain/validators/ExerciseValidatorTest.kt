package com.teodor.shared.domain.validators

import com.teodor.shared.domain.ValidationException
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        assertDoesNotThrow {
            ExerciseValidator.validate(exercise1)
        }

        val exercise2 = Exercise(
            id = 15,
            name = "Triceps Extension",
            mainMuscleGroup = MuscleGroup.TRICEPS,
            secondaryMuscleGroups = emptyList(),
            equipmentUsed = listOf(EquipmentType.CABLE_MACHINE),
            exerciseType = ExerciseType.REPETITION,
            userId = "1234"
        )
        assertDoesNotThrow {
            ExerciseValidator.validate(exercise2)
        }
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
        val exception1 = assertThrows<ValidationException> {
            ExerciseValidator.validate(exercise1)
        }
        val errorMessage1 = exception1.message
        assertTrue { errorMessage1?.contains("Id") == true }
        assertTrue { errorMessage1?.contains("Name") == true }
        assertTrue { errorMessage1?.contains("Main muscle group") == true }
        assertTrue { errorMessage1?.contains("secondary muscle groups") == true }
        assertTrue { errorMessage1?.contains("equipments") == true }
        assertTrue { errorMessage1?.contains("Exercise type") == true }

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
        val exception2 = assertThrows<ValidationException> {
            ExerciseValidator.validate(exercise2)
        }
        val errorMessage2 = exception2.message
        assertTrue { errorMessage2?.contains("Id") == true }
        assertTrue { errorMessage2?.contains("Main muscle group cannot be secondary as well") == true }
    }
}