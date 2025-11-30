package com.example.boxtrakr.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categories: MutableList<Category>,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit,
    onAddCategory: (String) -> Unit,
    // onAddBox now accepts categoryName and domain Box (keeps same app-level behaviour)
    onAddBox: (String, Box) -> Unit,
    onAddBoxContent: (String, String, Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showAddCatDialog by remember { mutableStateOf(false) }
    var showAddBoxDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Text(
                "Categories",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedCategory == null) showAddCatDialog = true
                    else showAddBoxDialog = true
                }
            ) { Text("+") }
        }
    ) { inner ->
        Box(Modifier.padding(inner).padding(16.dp)) {
            if (selectedCategory != null) {
                CategoryDetailScreen(
                    category = selectedCategory!!,
                    onBack = { selectedCategory = null },
                    allBoxes = allBoxes,
                    onBoxClick = onBoxClick
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    categories.forEach { category ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category }
                        ) {
                            Text(
                                category.name,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddCatDialog) {
        AlertDialog(
            onDismissRequest = { showAddCatDialog = false },
            title = { Text("New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = newCategoryName.trim()
                    if (name.isNotEmpty()) {
                        categories.add(Category(name))
                        onAddCategory(name)
                    }
                    newCategoryName = ""
                    showAddCatDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Box Dialog (uses onAdd returning a domain Box)
    if (showAddBoxDialog && selectedCategory != null) {
        AddBoxDialog(
            showDialog = showAddBoxDialog,
            onDismiss = { showAddBoxDialog = false },
            onAdd = { box ->
                val categoryName = selectedCategory!!.name

                // Persist via the app-level callback
                onAddBox(categoryName, box)

                // Add to local state (domain)
                selectedCategory!!.boxes.add(box)
                allBoxes.add(box)

                // Persist contents as separate DB records using onAddBoxContent
                box.contents.forEach { content ->
                    onAddBoxContent(box.name, content.name, content.quantity)
                }

                showAddBoxDialog = false
            }
        )
    }
}
