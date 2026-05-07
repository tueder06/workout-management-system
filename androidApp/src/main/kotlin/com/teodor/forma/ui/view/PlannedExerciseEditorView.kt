package com.teodor.forma.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teodor.forma.ui.theme.DarkGreen
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.forma.ui.theme.LightGreySurface
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.viewmodel.PlannedExerciseEditorState
import com.teodor.shared.viewmodel.PlannedExerciseEditorViewModel

@Composable
fun PlannedExerciseEditorView(
    viewModel: PlannedExerciseEditorViewModel,
    onNavigateBack: (Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveComplete) {
        if (state.saveComplete) {
            onNavigateBack(true)
        }
    }

    PlannedExerciseEditorContent(
        state = state,
        onBackClick = { onNavigateBack(false) },
        onEditDetailsClick = viewModel::enableEditMode,
        onExerciseSelect = viewModel::updateExercise,
        onSetsChange = viewModel::updateSets,
        onRepsChange = viewModel::updateReps,
        onWeightChange = viewModel::updateWeight,
        onDurationChange = viewModel::updateDuration,
        onPauseChange = viewModel::updatePause,
        onSaveClick = viewModel::saveExercise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannedExerciseEditorContent(
    state: PlannedExerciseEditorState,
    onBackClick: () -> Unit,
    onEditDetailsClick: () -> Unit,
    onExerciseSelect: (Exercise) -> Unit,
    onSetsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onPauseChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DeepCharcoal).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) }
                Text(
                    text = if (state.plannedExerciseId == null) "Add Exercise" else "Edit Exercise",
                    color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded && state.isEditing,
                        onExpandedChange = { if (state.isEditing) expanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.selectedExercise?.name ?: "Select Exercise",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exercise Movement") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = state.isEditing
                                )
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LightGreySurface,
                                unfocusedContainerColor = LightGreySurface,
                                focusedIndicatorColor = EmeraldGreen,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = DeepCharcoal,
                                unfocusedTextColor = DeepCharcoal
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            state.availableExercises.forEach { exercise ->
                                DropdownMenuItem(
                                    text = { Text(exercise.name) },
                                    onClick = {
                                        onExerciseSelect(exercise)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.targetSets,
                        onValueChange = onSetsChange,
                        label = { Text("Target Sets") },
                        readOnly = !state.isEditing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGreySurface,
                            unfocusedContainerColor = LightGreySurface,
                            focusedIndicatorColor = EmeraldGreen,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = DeepCharcoal,
                            unfocusedTextColor = DeepCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.selectedExercise != null) {
                        if (state.selectedExercise?.exerciseType == ExerciseType.REPETITION) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = state.targetReps,
                                    onValueChange = onRepsChange,
                                    label = { Text("Target Reps") },
                                    readOnly = !state.isEditing,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = LightGreySurface,
                                        unfocusedContainerColor = LightGreySurface,
                                        focusedIndicatorColor = EmeraldGreen,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = DeepCharcoal,
                                        unfocusedTextColor = DeepCharcoal
                                    )
                                )
                                OutlinedTextField(
                                    value = state.targetWeight,
                                    onValueChange = onWeightChange,
                                    label = { Text("Weight (kg)") },
                                    readOnly = !state.isEditing,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = LightGreySurface,
                                        unfocusedContainerColor = LightGreySurface,
                                        focusedIndicatorColor = EmeraldGreen,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = DeepCharcoal,
                                        unfocusedTextColor = DeepCharcoal
                                    )
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = state.targetDuration,
                                onValueChange = onDurationChange,
                                label = { Text("Duration (seconds)") },
                                readOnly = !state.isEditing,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = LightGreySurface,
                                    unfocusedContainerColor = LightGreySurface,
                                    focusedIndicatorColor = EmeraldGreen,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = DeepCharcoal,
                                    unfocusedTextColor = DeepCharcoal
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = state.pauseDurationSeconds,
                        onValueChange = onPauseChange,
                        label = { Text("Pause Between Sets (sec)") },
                        readOnly = !state.isEditing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGreySurface,
                            unfocusedContainerColor = LightGreySurface,
                            focusedIndicatorColor = EmeraldGreen,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = DeepCharcoal,
                            unfocusedTextColor = DeepCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!state.isEditing) {
                        Button(
                            onClick = onEditDetailsClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) { Text("Edit Details", color = Color.White, fontWeight = FontWeight.Bold) }
                    } else {
                        Button(
                            onClick = onSaveClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepCharcoal)
                            else Text("Save Exercise", color = DeepCharcoal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}