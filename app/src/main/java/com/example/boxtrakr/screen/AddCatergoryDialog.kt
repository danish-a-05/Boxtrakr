package com.example.boxtrakr.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddCategoryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var newCategory by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Category") },
            text = {
                Column {
                    Text("Enter category name:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        placeholder = { Text("Category name") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategory.isNotBlank()) {
                        onAdd(newCategory)
                        newCategory = ""
                    }
                }) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
