package com.example.boxtrakr.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.BoxContent

@Composable
fun AddBoxDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (Box) -> Unit
) {
    var newBoxName by remember { mutableStateOf("") }
    val newBoxContents = remember { mutableStateListOf<BoxContent>() }
    var contentName by remember { mutableStateOf("") }
    var contentQuantity by remember { mutableStateOf("") }

    // Private box toggle and password
    var isPrivate by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Box") },
            text = {
                Column {
                    Text("Box Name:")
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = newBoxName,
                        onValueChange = { newBoxName = it },
                        placeholder = { Text("Box Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add Content", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column {
                        TextField(
                            value = contentName,
                            onValueChange = { contentName = it },
                            placeholder = { Text("Content Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            TextField(
                                value = contentQuantity,
                                onValueChange = { contentQuantity = it },
                                placeholder = { Text("Qty") },
                                modifier = Modifier.width(110.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = {
                                if (contentName.isNotBlank() && contentQuantity.toIntOrNull() != null) {
                                    newBoxContents.add(BoxContent(contentName, contentQuantity.toInt()))
                                    contentName = ""
                                    contentQuantity = ""
                                }
                            }) {
                                Text("Add Content")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Private toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Private box?")
                        Switch(checked = isPrivate, onCheckedChange = {
                            isPrivate = it
                            if (!it) {
                                // clear password when toggling off
                                password = ""
                                passwordError = ""
                            }
                        })
                    }

                    // Password input when private
                    if (isPrivate) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = ""
                            },
                            label = { Text("Password (min 6 chars)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (passwordError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(passwordError, color = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    newBoxContents.forEach { c ->
                        Text("- ${c.name} x${c.quantity}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Validation
                    if (newBoxName.isBlank()) return@TextButton

                    if (isPrivate) {
                        if (password.length < 6) {
                            passwordError = "Password must be at least 6 characters"
                            return@TextButton
                        }
                    }

                    // Create domain Box (with privacy fields)
                    val newBox = Box(
                        name = newBoxName,
                        contents = newBoxContents.toMutableList(),
                        isPrivate = isPrivate,
                        password = if (isPrivate) password else null
                    )

                    // Clear UI state
                    newBoxName = ""
                    newBoxContents.clear()
                    isPrivate = false
                    password = ""
                    passwordError = ""

                    onAdd(newBox)

                }) {
                    Text("Finish", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }
}
