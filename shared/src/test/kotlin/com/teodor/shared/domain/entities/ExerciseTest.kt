package com.teodor.shared.domain.entities

import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExerciseTest {
    private val jsonConfig = Json { ignoreUnknownKeys = true }

    @Test
    fun `Exercise isCustom is set correctly`() {
        val predefinedExercise = Exercise (
            id = 12,
            name = "Pull Up",
            mainMuscleGroup = MuscleGroup.BACK,
            exerciseType = ExerciseType.REPETITION,
            userId = null,
        )
        assertTrue { !predefinedExercise.isCustom }

        val customExercise = Exercise(
            id = 13,
            name = "Treadmill run",
            mainMuscleGroup = MuscleGroup.CARDIO,
            exerciseType = ExerciseType.DURATION,
            userId = "1234",
        )
        assertTrue { customExercise.isCustom }
    }

    @Test
    fun `Exercise entity serializes and deserializes correctly`() {
        val originalExercise = Exercise(
            id = 12,
            name = "Inclined Chest Press",
            mainMuscleGroup = MuscleGroup.CHEST,
            secondaryMuscleGroups = listOf(
                MuscleGroup.SHOULDERS,
                MuscleGroup.TRICEPS,
            ),
            equipmentUsed = listOf(
                EquipmentType.BENCH,
                EquipmentType.DUMBBELL,
            ),
            exerciseType = ExerciseType.REPETITION,
            userId = null,
        )

        val jsonString = jsonConfig.encodeToString(originalExercise)
        val decodedExercise = jsonConfig.decodeFromString<Exercise>(jsonString)

        assertEquals(originalExercise.id, decodedExercise.id, "Id mapping failed")
        assertEquals(originalExercise.name, decodedExercise.name, "Name mapping failed")
        assertEquals(originalExercise.mainMuscleGroup, decodedExercise.mainMuscleGroup, "Main muscle group mapping failed")
        assertEquals(originalExercise.secondaryMuscleGroups, decodedExercise.secondaryMuscleGroups, "Secondary muscle groups mapping failed")
        assertEquals(originalExercise.equipmentUsed, decodedExercise.equipmentUsed, "Equipments mapping failed")
        assertEquals(originalExercise.exerciseType, decodedExercise.exerciseType, "Exercise type mapping failed")
        assertEquals(originalExercise.userId, decodedExercise.userId, "User id mapping failed")
    }
}