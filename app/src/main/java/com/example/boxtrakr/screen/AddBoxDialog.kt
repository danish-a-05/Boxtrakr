package com.example.boxtrakr.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Box") },
            text = {
                Column {
                    Text("Box Name:")
                    TextField(
                        value = newBoxName,
                        onValueChange = { newBoxName = it },
                        placeholder = { Text("Box Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add Content")
                    Row {
                        TextField(
                            value = contentName,
                            onValueChange = { contentName = it },
                            placeholder = { Text("Content Name") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = contentQuantity,
                            onValueChange = { contentQuantity = it },
                            placeholder = { Text("Quantity") },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (contentName.isNotBlank() && contentQuantity.toIntOrNull() != null) {
                                newBoxContents.add(
                                    BoxContent(contentName, contentQuantity.toInt())
                                )
                                contentName = ""
                                contentQuantity = ""
                            }
                        }) {
                            Text("Add Content")
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
                    if (newBoxName.isNotBlank()) {
                        val newBox = Box(newBoxName, newBoxContents.toMutableList())
                        onAdd(newBox)
                        newBoxName = ""
                        newBoxContents.clear()
                    }
                }) {
                    Text("Finish", fontWeight = FontWeight.Bold)
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
