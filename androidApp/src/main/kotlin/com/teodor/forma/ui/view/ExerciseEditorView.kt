package com.teodor.forma.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teodor.forma.ConfirmDialog
import com.teodor.forma.MultiSelectDialog
import com.teodor.forma.ui.theme.DarkGreen
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.LightGreySurface
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import com.teodor.shared.viewmodel.ExerciseEditorState
import com.teodor.shared.viewmodel.ExerciseEditorViewModel

@Composable
fun ExerciseEditorView(
    viewModel: ExerciseEditorViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saveComplete) {
        if (state.saveComplete) onNavigateBack()
    }

    if (state.showDeleteDialog) {
        ConfirmDialog(
            title = "Delete Exercise?",
            message = "Are you sure you want to delete '${state.draftName}'? This action cannot be undone.",
            isDestructive = true,
            isLoading = state.isDeleting,
            onDismiss = { viewModel.toggleDeleteDialog() },
            onConfirm = { viewModel.deleteExercise() }
        )
    }

    ExerciseEditorContent(
        state = state,
        onBackClick = onNavigateBack,
        onNameChange = viewModel::updateName,
        onTypeChange = viewModel::setExerciseType,
        onEditClick = viewModel::enableEditMode,
        onSaveClick = viewModel::saveExercise,
        onDeleteClick = viewModel::toggleDeleteDialog,
        onMainMuscleSelect = viewModel::updateMainMuscle,
        onSecondaryMuscleToggle = viewModel::toggleSecondaryMuscle,
        onEquipmentToggle = viewModel::toggleEquipment
    )
}

@Composable
fun ExerciseEditorContent(
    state: ExerciseEditorState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onTypeChange: (ExerciseType) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMainMuscleSelect: (MuscleGroup) -> Unit,
    onSecondaryMuscleToggle: (MuscleGroup) -> Unit,
    onEquipmentToggle: (EquipmentType) -> Unit
) {
    var showMainMuscleDropdown by remember { mutableStateOf(false) }
    var showSecondaryMuscleDialog by remember { mutableStateOf(false) }
    var showEquipmentDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(DeepCharcoal)) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = state.draftName.ifEmpty { if (state.isLoadingInitialData) "View exercise" else "New exercise"  },
                color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            if (state.isLoadingInitialData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            } else {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isEditing) "Edit Information" else "View Information",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                        if (state.exerciseId != null && state.isCustom) {
                            IconButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FormLabel("Name")
                    CustomTextField(
                        value = state.draftName,
                        onValueChange = onNameChange,
                        isEditing = state.isEditing
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Main Muscle Group")
                    Box {
                        SelectionBox(
                            text = state.draftMainMuscle?.name ?: "Select Main Muscle",
                            isEditing = state.isEditing,
                            isPrimary = true,
                            onClick = { showMainMuscleDropdown = true }
                        )

                        DropdownMenu(
                            expanded = showMainMuscleDropdown,
                            onDismissRequest = { showMainMuscleDropdown = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            MuscleGroup.entries.forEach { muscle ->
                                DropdownMenuItem(
                                    text = { Text(muscle.name) },
                                    onClick = {
                                        onMainMuscleSelect(muscle)
                                        showMainMuscleDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Secondary Muscle Groups")
                    SelectionBox(
                        text = if (state.draftSecondaryMuscles.isEmpty()) "None" else state.draftSecondaryMuscles.joinToString { it.name },
                        isEditing = state.isEditing,
                        onClick = { showSecondaryMuscleDialog = true }
                    )

                    if (showSecondaryMuscleDialog) {
                        MultiSelectDialog(
                            title = "Select Secondary Muscles",
                            items = MuscleGroup.entries,
                            selectedItems = state.draftSecondaryMuscles,
                            onToggle = onSecondaryMuscleToggle,
                            onDismiss = { showSecondaryMuscleDialog = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Equipment Used")
                    SelectionBox(
                        text = if (state.draftEquipment.isEmpty()) "Select equipment" else state.draftEquipment.joinToString { it.name },
                        isEditing = state.isEditing,
                        onClick = { showEquipmentDialog = true }
                    )

                    if (showEquipmentDialog) {
                        MultiSelectDialog(
                            title = "Select Equipment",
                            items = EquipmentType.entries,
                            selectedItems = state.draftEquipment,
                            onToggle = onEquipmentToggle,
                            onDismiss = { showEquipmentDialog = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FormLabel("Exercise Type")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.draftExerciseType == ExerciseType.REPETITION,
                            onClick = { if (state.isEditing) onTypeChange(ExerciseType.REPETITION) },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkGreen)
                        )
                        Text("Repetition", fontWeight = FontWeight.Bold, color = DeepCharcoal)

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = state.draftExerciseType == ExerciseType.DURATION,
                            onClick = { if (state.isEditing) onTypeChange(ExerciseType.DURATION) },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkGreen)
                        )
                        Text("Duration", fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    if (state.errorMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = state.errorMessage ?: "",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (state.isCustom) {
                        Button(
                            onClick = if (state.isEditing) onSaveClick else onEditClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = if (state.isEditing) "Save Details" else "Edit Details",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "System exercises cannot be edited.",
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = DeepCharcoal,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean
) {
    BasicTextField(
        value = value,
        onValueChange = { if (isEditing) onValueChange(it) },
        readOnly = !isEditing,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepCharcoal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightGreySurface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@Composable
fun SelectionBox(
    text: String,
    isEditing: Boolean,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isPrimary) DarkGreen else LightGreySurface
    val textColor = if (isPrimary) Color.White else DeepCharcoal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(enabled = isEditing) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 16.sp
        )
    }
}