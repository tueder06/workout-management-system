package com.teodor.shared.domain.entities

import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Exercise (
    override val id: Long? = null,
    val name: String,

    @SerialName("main_muscle_group")
    val mainMuscleGroup: MuscleGroup?,

    @SerialName("secondary_muscle_groups")
    val secondaryMuscleGroups: List<MuscleGroup> = emptyList(),

    @SerialName("equipment_used")
    val equipmentUsed: List<EquipmentType> = emptyList(),

    @SerialName("exercise_type")
    val exerciseType: ExerciseType?,

    @SerialName("user_id")
    val userId: String? = null,
) : Entity<Long> {
    @Transient
    val isCustom: Boolean = userId != null
}