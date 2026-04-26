package com.teodor.forma.ui.view

import androidx.compose.material3.HorizontalDivider
import com.teodor.forma.ui.theme.DarkGreen
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.viewmodel.ExerciseLibraryState
import com.teodor.shared.viewmodel.ExerciseLibraryViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.teodor.forma.FormaTextField
import com.teodor.forma.ui.theme.LightGreySurface
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup

@Composable
fun ExerciseLibraryView(
    viewModel: ExerciseLibraryViewModel,
    onNavigateToEditor: (Long?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ExerciseLibraryContent(
        state = state,
        onAddClick = { onNavigateToEditor(null) },
        onExerciseClick = { exercise -> onNavigateToEditor(exercise.id) },
        onFilterSearchChange = { viewModel.updateSearchQuery(it) },
        onFilterTypeSelect = { viewModel.updateTypeFilter(it) },
        onFilterMuscleSelect = { viewModel.updateMuscleFilter(it) },
        onFilterEquipmentSelect = { viewModel.updateEquipmentFilter(it) }
    )
}

@Composable
fun ExerciseLibraryContent(
    state: ExerciseLibraryState,
    onAddClick: () -> Unit,
    onExerciseClick: (Exercise) -> Unit,
    onFilterSearchChange: (String) -> Unit,
    onFilterTypeSelect: (ExerciseType?) -> Unit,
    onFilterMuscleSelect: (MuscleGroup?) -> Unit,
    onFilterEquipmentSelect: (EquipmentType?) -> Unit
) {
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showMuscleDropdown by remember { mutableStateOf(false) }
    var showEquipmentDropdown by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(DeepCharcoal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "View exercises", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onAddClick, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add", tint = Color.White, modifier = Modifier.fillMaxSize())
            }
        }

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Filter your results:", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = DeepCharcoal, modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FormaTextField(
                        value = state.currentFilter.nameSearchQuery ?: "",
                        onValueChange = onFilterSearchChange,
                        label = "Name"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Box {
                                val currentType = state.currentFilter.exerciseType
                                val chipText = currentType?.name ?: "All Types"

                                FilterChip(text = chipText, onClick = { showTypeDropdown = true })

                                DropdownMenu(expanded = showTypeDropdown, onDismissRequest = { showTypeDropdown = false }, modifier = Modifier.background(Color.White)) {
                                    DropdownMenuItem(
                                        text = { Text("All Types", fontWeight = FontWeight.Bold) },
                                        onClick = { onFilterTypeSelect(null); showTypeDropdown = false }
                                    )
                                    HorizontalDivider(color = LightGreySurface)
                                    ExerciseType.entries.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.name) },
                                            onClick = { onFilterTypeSelect(type); showTypeDropdown = false }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Box {
                                val currentMuscle = state.currentFilter.muscleGroup
                                val chipText = currentMuscle?.name ?: "All muscle groups"

                                FilterChip(text = chipText, onClick = { showMuscleDropdown = true })

                                DropdownMenu(expanded = showMuscleDropdown, onDismissRequest = { showMuscleDropdown = false }, modifier = Modifier.background(Color.White)) {
                                    DropdownMenuItem(
                                        text = { Text("All muscle groups", fontWeight = FontWeight.Bold) },
                                        onClick = { onFilterMuscleSelect(null); showMuscleDropdown = false }
                                    )
                                    HorizontalDivider(color = LightGreySurface)
                                    MuscleGroup.entries.forEach { muscle ->
                                        DropdownMenuItem(
                                            text = { Text(muscle.name) },
                                            onClick = { onFilterMuscleSelect(muscle); showMuscleDropdown = false }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Box {
                                val currentEquipment = state.currentFilter.equipmentType
                                val chipText = currentEquipment?.name ?: "All Equipments"

                                FilterChip(text = chipText, onClick = { showEquipmentDropdown = true })

                                DropdownMenu(expanded = showEquipmentDropdown, onDismissRequest = { showEquipmentDropdown = false }, modifier = Modifier.background(Color.White)) {
                                    DropdownMenuItem(
                                        text = { Text("All Equipments", fontWeight = FontWeight.Bold) },
                                        onClick = { onFilterEquipmentSelect(null); showEquipmentDropdown = false }
                                    )
                                    HorizontalDivider(color = LightGreySurface)
                                    EquipmentType.entries.forEach { eq ->
                                        DropdownMenuItem(
                                            text = { Text(eq.name) },
                                            onClick = { onFilterEquipmentSelect(eq); showEquipmentDropdown = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = LightGreySurface, thickness = 1.dp)

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DarkGreen)
                    }
                } else if (state.exercises.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No exercises found.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.exercises) { exercise ->
                            ExerciseCard(exercise = exercise, onClick = { onExerciseClick(exercise) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(DarkGreen)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    val allMuscles = listOf(exercise.mainMuscleGroup) + exercise.secondaryMuscleGroups
    val musclesText = allMuscles.filterNotNull().joinToString(", ") { muscle ->
        muscle.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCharcoal,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(text = musclesText, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepCharcoal)
            }
            Text(text = "View details →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepCharcoal)
        }
    }
}