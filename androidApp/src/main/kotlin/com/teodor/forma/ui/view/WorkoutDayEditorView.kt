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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teodor.forma.ConfirmDialog
import com.teodor.forma.R
import com.teodor.forma.ui.theme.DarkGreen
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.shared.domain.entities.PlannedExercise
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.viewmodel.WorkoutDayEditorState
import com.teodor.shared.viewmodel.WorkoutDayEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WorkoutDayEditorView(
    viewModel: WorkoutDayEditorViewModel = koinViewModel(),
    onNavigateBack: (Boolean) -> Unit,
    onNavigateToExerciseEditor: (Long?) -> Unit,
    onStartWorkout: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveComplete) {
        if (state.saveComplete) {
            onNavigateBack(true)
        }
    }

    WorkoutDayEditorContent(
        state = state,
        onBackClick = onNavigateBack,
        onNameChange = viewModel::updateName,
        onSaveDay = viewModel::saveDay,
        onEditDetailsClick = viewModel::enableEditMode,
        onAddExerciseClick = { onNavigateToExerciseEditor(null) },
        onExerciseClick = onNavigateToExerciseEditor,
        onStartWorkoutClick = { state.dayId?.let { onStartWorkout(it) } },
        onPromptDeleteExercise = viewModel::promptDeleteExercise,
        onDismissDeleteExercise = viewModel::dismissDeleteExerciseDialog,
        onConfirmDeleteExercise = viewModel::confirmDeleteExercise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDayEditorContent(
    state: WorkoutDayEditorState,
    onBackClick: (Boolean) -> Unit,
    onNameChange: (String) -> Unit,
    onSaveDay: () -> Unit,
    onEditDetailsClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    onExerciseClick: (Long?) -> Unit,
    onStartWorkoutClick: () -> Unit,
    onPromptDeleteExercise: (Long?) -> Unit,
    onDismissDeleteExercise: () -> Unit,
    onConfirmDeleteExercise: () -> Unit
) {
    if (state.exerciseIdToDelete != null) {
        ConfirmDialog(
            "Delete Exercise",
            "Are you sure you want to remove this exercise from the day?",
            state.isDeletingExercise,
            onDismissDeleteExercise,
            onConfirmDeleteExercise
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                IconButton(onClick = { onBackClick(true) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back", tint = Color.White)
                }
                Text(
                    text = if (state.dayId == null) "Create Day" else "Edit Day",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
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

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormLabel("Name")
                    CustomTextField(
                        value = state.draftName,
                        onValueChange = onNameChange,
                        isEditing = state.isEditing
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!state.isEditing) {
                        Button(
                            onClick = onEditDetailsClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text("Edit Details", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onSaveDay,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepCharcoal, strokeWidth = 2.dp)
                            } else {
                                Text("Save Day", color = DeepCharcoal, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (state.dayId != null || state.exercises.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.draftName.ifBlank { "Exercises" },
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onStartWorkoutClick() }
                    ) {
                        Text(
                            text = "Start\nWorkout",
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.PlayCircleOutline,
                            contentDescription = "Start Workout",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            itemsIndexed(state.exercises) { index, exercise ->
                PlannedExerciseCard(
                    index = index + 1,
                    exercise = exercise,
                    onClick = { onExerciseClick(exercise.id) },
                    onDelete = { onPromptDeleteExercise(exercise.id) }
                )
            }

            item {
                Button(
                    onClick = onAddExerciseClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepCharcoal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                ) {
                    Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Exercise", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun PlannedExerciseCard(
    index: Int,
    exercise: PlannedExercise,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isDurationType = exercise.exerciseDetails?.exerciseType == ExerciseType.DURATION

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$index. ${exercise.exerciseDetails?.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp).padding(start = 8.dp)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "No. sets: ${exercise.targetSets}", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)

                    if (isDurationType) {
                        exercise.targetDurationSeconds?.let { duration ->
                            Text(text = "Duration: ${duration}s", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        exercise.targetReps?.let { reps ->
                            Text(text = "No. reps: $reps", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        exercise.targetWeight?.let { weight ->
                            val formattedWeight = if (weight % 1f == 0f) "${weight.toInt()} kg" else "$weight kg"
                            Text(text = "Weight: $formattedWeight", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Pause: ${exercise.pauseDurationSeconds}s",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .paint(
                            painterResource(id = R.drawable.not_found),
                            contentScale = ContentScale.Inside
                        )
                )
            }
        }
    }
}