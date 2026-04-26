package com.teodor.shared.viewmodel

import com.teodor.shared.domain.ExerciseFilter
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String? = null,

    val draftUsername: String = "",
    val draftEmail: String = "",
    val draftFirstName: String = "",
    val draftLastName: String = "",
    val draftPassword: String = "",
    val draftConfirmPassword: String = "",

    val editingFields: Set<String> = emptySet(),

    val showSaveDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,

    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val isLoggedOut: Boolean = false
) {
    val hasUnsavedChanges: Boolean
        get() = username != draftUsername ||
                email != draftEmail ||
                firstName != draftFirstName ||
                lastName != draftLastName ||
                draftPassword.isNotEmpty()
}

data class ExerciseLibraryState(
    val exercises: List<Exercise> = emptyList(),
    val currentFilter: ExerciseFilter = ExerciseFilter(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ExerciseEditorState(
    val exerciseId: Long? = null,
    val isCustom: Boolean = true,
    val draftName: String = "",

    val draftMainMuscle: MuscleGroup? = null,
    val draftSecondaryMuscles: List<MuscleGroup> = emptyList(),
    val draftEquipment: List<EquipmentType> = emptyList(),
    val draftExerciseType: ExerciseType? = null,

    val isLoadingInitialData: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveComplete: Boolean = false
)