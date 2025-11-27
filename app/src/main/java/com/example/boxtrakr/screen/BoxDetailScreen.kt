package com.example.boxtrakr.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.BoxContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxDetailScreen(
    box: Box,
    onBack: () -> Unit,
    onAddContent: (String, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newQty by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Text(
                    box.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onBack) {
                    Text("Back")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(box.contents) { item ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.name, fontWeight = FontWeight.Medium)
                        Text("Quantity: ${item.quantity}")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Item Name") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newQty,
                        onValueChange = { newQty = it },
                        label = { Text("Quantity") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val q = newQty.toIntOrNull()
                    if (newName.isNotBlank() && q != null) {
                        box.contents.add(BoxContent(newName, q))
                        onAddContent(newName, q)
                    }
                    newName = ""
                    newQty = ""
                    showDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
