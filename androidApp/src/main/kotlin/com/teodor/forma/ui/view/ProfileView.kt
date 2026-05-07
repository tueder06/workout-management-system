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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.teodor.forma.ConfirmDialog
import com.teodor.forma.ui.theme.AvatarPurple
import com.teodor.forma.ui.theme.DangerRed
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.forma.ui.theme.LightGreySurface
import com.teodor.shared.viewmodel.ProfileViewModel

@Composable
fun ProfileView(
    viewModel: ProfileViewModel,
) {
    val state by viewModel.state.collectAsState()

    if (state.showSaveDialog) {
        ConfirmDialog(
            "Save Changes",
            "Are you sure you want to commit these changes?",
            state.isActionLoading,
            viewModel::dismissDialogs,
            viewModel::confirmSave
        )
    }
    if (state.showLogoutDialog) {
        ConfirmDialog(
            "Log Out",
            "Are you sure you want to log out?",
            state.isActionLoading,
            viewModel::dismissDialogs,
            viewModel::confirmLogout
        )
    }
    if (state.showDeleteDialog) {
        ConfirmDialog(
            "Delete Account",
            "This action will be permanent. Proceed?",
            state.isActionLoading,
            viewModel::dismissDialogs,
            viewModel::confirmDelete,
            isDestructive = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
    ) {
        if (state.errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.errorMessage ?: "Failed to save details",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { viewModel.clearError() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(AvatarPurple),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.profileImageUrl != null) {
                        AsyncImage(
                            model = state.profileImageUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    state.username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "View Details",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ProfileInteractiveField(
                            "Username",
                            state.draftUsername,
                            state.editingFields.contains("username"),
                            { viewModel.toggleEditMode("username") }) {
                            viewModel.onDraftChange(
                                "username",
                                it
                            )
                        }

                        ProfileInteractiveField(
                            "Password",
                            state.draftPassword,
                            state.editingFields.contains("password"),
                            { viewModel.toggleEditMode("password") },
                            isPassword = true
                        ) { viewModel.onDraftChange("password", it) }

                        if (state.editingFields.contains("password")) {
                            ProfileInteractiveField(
                                "Confirm Password",
                                state.draftConfirmPassword,
                                true,
                                {},
                                isPassword = true
                            ) { viewModel.onDraftChange("confirmPassword", it) }
                        }

                        ProfileInteractiveField(
                            "Email Address",
                            state.draftEmail,
                            state.editingFields.contains("email"),
                            { viewModel.toggleEditMode("email") }
                        ) { viewModel.onDraftChange("email", it) }
                        ProfileInteractiveField(
                            "First Name",
                            state.draftFirstName,
                            state.editingFields.contains("firstName"),
                            { viewModel.toggleEditMode("firstName") }
                        ) { viewModel.onDraftChange("firstName", it) }
                        ProfileInteractiveField(
                            "Last Name",
                            state.draftLastName,
                            state.editingFields.contains("lastName"),
                            { viewModel.toggleEditMode("lastName") }
                        ) { viewModel.onDraftChange("lastName", it) }
                    }
                }

                if (state.hasUnsavedChanges) {
                    Button(
                        onClick = { viewModel.onSaveClicked() },
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        else Text(
                            "Save Details",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.onLogoutClicked() },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Log Out")
                }
            }

            IconButton(
                onClick = { viewModel.onDeleteClicked() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(DangerRed, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Account",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun ProfileInteractiveField(
    label: String,
    value: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier.weight(1f).background(LightGreySurface, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (isEditing && label != "Email Address") {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val displayValue = if (isPassword) "••••••••••••••" else value
                    Text(displayValue, color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (label != "Confirm Password" && label != "Email Address") {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit $label",
                    tint = if (isEditing) Color.Gray else Color.Black,
                    modifier = Modifier.size(24.dp).clickable { onEditClick() }
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}