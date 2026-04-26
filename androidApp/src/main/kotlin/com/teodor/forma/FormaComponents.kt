package com.teodor.forma

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teodor.forma.ui.theme.DangerRed
import com.teodor.forma.ui.theme.DarkGreen
import com.teodor.forma.ui.theme.DeepCharcoal
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.forma.ui.theme.LightGreySurface

@Composable
fun FormaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
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

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) DangerRed else EmeraldGreen
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = if (isDestructive) Color.White else Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirm", color = if (isDestructive) Color.White else Color.Black)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun <T : Enum<T>> MultiSelectDialog(
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    onToggle: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = DeepCharcoal) },
        text = {
            LazyColumn {
                items(items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(item) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedItems.contains(item),
                            onCheckedChange = { onToggle(item) },
                            colors = CheckboxDefaults.colors(checkedColor = DarkGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.name, color = DeepCharcoal, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = DarkGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}