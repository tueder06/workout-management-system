package com.teodor.shared.domain

import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup

data class ExerciseFilter(
    val nameSearchQuery: String? = null,
    val muscleGroup: MuscleGroup? = null,
    val equipmentType: EquipmentType? = null,
    val exerciseType: ExerciseType? = null,
    val userId: String? = null,
)