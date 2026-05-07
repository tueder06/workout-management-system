package com.teodor.forma.ui.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teodor.forma.ConfirmDialog
import com.teodor.forma.R
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.forma.ui.theme.LightGreySurface
import com.teodor.shared.domain.entities.WorkoutPlan
import com.teodor.shared.viewmodel.WorkoutPlanState
import com.teodor.shared.viewmodel.WorkoutPlanViewModel

@Composable
fun WorkoutPlanView(
    viewModel: WorkoutPlanViewModel,
    onNavigateToEditor: (Long?) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    WorkoutPlansContent(
        state = state,
        onNavigateToEditor = onNavigateToEditor,
        onPromptDelete = viewModel::promptDelete,
        onDismissDialog = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete
    )
}

@Composable
fun WorkoutPlansContent(
    state: WorkoutPlanState,
    onNavigateToEditor: (Long?) -> Unit,
    onPromptDelete: (Long?) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    if(state.planToDelete != null) {
        ConfirmDialog(
            "Delete Plan",
            "Are you sure you want to delete this workout plan? Everything related to it will be lost.",
            state.isDeleting,
            onDismissDialog,
            onConfirmDelete
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workout plans",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onNavigateToEditor(null) }) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        contentDescription = "Create Workout Plan",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        if (state.isLoading) {
            item {
                LinearProgressIndicator(
                    color = EmeraldGreen,
                    trackColor = DeepCharcoal,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            if (state.workoutPlans.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No workout plans yet.\nTap + to create one!",
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(state.workoutPlans) { plan ->
                    WorkoutPlanCard(
                        plan = plan,
                        onClick = { onNavigateToEditor(plan.id) },
                        onDelete = { onPromptDelete(plan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanCard(
    plan: WorkoutPlan,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreySurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepCharcoal,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "View details \u2192",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Plan",
                            tint = DeepCharcoal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .background(EmeraldGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = plan.description,
                            color = DeepCharcoal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Goal: ${plan.goal?.name}",
                            color = DeepCharcoal,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .paint(
                            painterResource(id = R.drawable.not_found),
                            contentScale = ContentScale.FillBounds)
                )
            }
        }
    }
}
