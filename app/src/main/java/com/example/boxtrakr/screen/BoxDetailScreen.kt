package com.example.boxtrakr.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.BoxContent

@Composable
fun BoxDetailScreen(
    box: Box,
    onBack: () -> Unit,
    onAddContent: (String, Int) -> Unit  // new callback: contentName, quantity
) {
    var showAddContentDialog by remember { mutableStateOf(false) }
    var newContentName by remember { mutableStateOf("") }
    var newContentQty by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Back",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBack() }
        )
        Spacer(modifier = Modifier.height(8.dp))

        box.contents.forEach { item ->
            Text("- ${item.name} x${item.quantity}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddContentDialog = true },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+", fontWeight = FontWeight.Bold)
        }

        if (showAddContentDialog) {
            AlertDialog(
                onDismissRequest = { showAddContentDialog = false },
                title = { Text("Add item to Box") },
                text = {
                    Column {
                        TextField(
                            value = newContentName,
                            onValueChange = { newContentName = it },
                            placeholder = { Text("Item Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = newContentQty,
                            onValueChange = { newContentQty = it },
                            placeholder = { Text("Qty") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val qty = newContentQty.toIntOrNull()
                        if (newContentName.isNotBlank() && qty != null) {
                            box.contents.add(BoxContent(newContentName, qty))
                            onAddContent(newContentName, qty) // save to Room
                        }
                        newContentName = ""
                        newContentQty = ""
                        showAddContentDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContentDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

