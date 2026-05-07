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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.teodor.shared.domain.WorkoutDayStats
import com.teodor.shared.domain.enums.TrainingGoal
import com.teodor.shared.viewmodel.WorkoutPlanEditorState
import com.teodor.shared.viewmodel.WorkoutPlanEditorViewModel

@Composable
fun WorkoutPlanEditorView(
    viewModel: WorkoutPlanEditorViewModel,
    onNavigateBack: (Boolean) -> Unit,
    onNavigateToDayEditor: (Long?) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saveComplete) {
        if (state.saveComplete) {
            onNavigateBack(true)
        }
    }

    WorkoutPlanEditorContent(
        state = state,
        onBackClick = onNavigateBack,
        onNameChange = viewModel::updateName,
        onDescriptionChange = viewModel::updateDescription,
        onGoalChange = viewModel::updateGoal,
        onSavePlan = viewModel::savePlan,
        onEditDetailsClick = viewModel::enableEditMode,
        onNavigateToDay = onNavigateToDayEditor,
        onPromptDeleteDay = viewModel::promptDeleteDay,
        onDismissDeleteDay = viewModel::dismissDeleteDayDialog,
        onConfirmDeleteDay = viewModel::confirmDeleteDay
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanEditorContent(
    state: WorkoutPlanEditorState,
    onBackClick: (Boolean) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onGoalChange: (TrainingGoal) -> Unit,
    onSavePlan: () -> Unit,
    onEditDetailsClick: () -> Unit,
    onNavigateToDay: (Long?) -> Unit,
    onPromptDeleteDay: (Long?) -> Unit,
    onDismissDeleteDay: () -> Unit,
    onConfirmDeleteDay: () -> Unit
) {
    if (state.dayIdToDelete != null) {
        ConfirmDialog(
            "Delete Day",
            "Are you sure you want to delete this workout day? All exercises inside it will be removed.",
            state.isDeletingDay,
            onDismissDeleteDay,
            onConfirmDeleteDay
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                IconButton(onClick = { onBackClick(false) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = if (state.planId == null) "Create Plan" else "Edit Plan",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
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
                    Text("Plan Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepCharcoal)
                    Spacer(modifier = Modifier.height(12.dp))

                    FormLabel("Name")
                    CustomTextField(
                        value = state.draftName,
                        onValueChange = onNameChange,
                        isEditing = state.isEditing
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FormLabel("Description")
                    CustomTextField(
                        value = state.draftDescription,
                        onValueChange = onDescriptionChange,
                        isEditing = state.isEditing
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var showGoalDropdown by remember { mutableStateOf(false) }
                    FormLabel("Training Goal")
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectionBox(
                            text = state.draftGoal?.name ?: "Select Training Goal",
                            isEditing = !state.isSaving,
                            isPrimary = true,
                            onClick = { showGoalDropdown = true }
                        )

                        DropdownMenu(
                            expanded = showGoalDropdown,
                            onDismissRequest = { showGoalDropdown = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            TrainingGoal.entries.forEach { goal ->
                                DropdownMenuItem(
                                    text = { Text(goal.name, color = DeepCharcoal) },
                                    onClick = {
                                        onGoalChange(goal)
                                        showGoalDropdown = false
                                    }
                                )
                            }
                        }
                    }

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
                            onClick = onSavePlan,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepCharcoal, strokeWidth = 2.dp)
                            } else {
                                Text("Save Plan", color = DeepCharcoal, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

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
                }
            }
        }

        if (state.planId != null || state.draftDays.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workout Days",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToDay(null) }
                    ) {
                        Text("+ Add Day", color = Color.White, fontSize = 12.sp, lineHeight = 14.sp)
                    }
                }
            }

            items(state.draftDays) { day ->
                WorkoutDayCard(
                    day = day,
                    onViewExercises = { onNavigateToDay(day.id) },
                    onDelete = { onPromptDeleteDay(day.id) }
                )
            }
        }
    }
}

@Composable
fun WorkoutDayCard(
    day: WorkoutDayStats,
    onViewExercises: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewExercises() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .paint(
                            painterResource(id = R.drawable.not_found),
                            contentScale = ContentScale.Inside
                        )
                )

                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatText("Duration", day.duration)
                    StatText("Weight", day.totalWeight)
                    StatText("Number of exercises", day.exerciseCount.toString())
                    StatText("Last time done", day.lastSession)
                }
            }
        }
    }
}

@Composable
fun StatText(label: String, value: String) {
    Text(
        text = "$label: $value",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}