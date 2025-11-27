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
import com.example.boxtrakr.domain.*

@Composable
fun CategoriesScreen(
    categories: MutableList<Category>,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit,
    onAddCategory: (String) -> Unit,      // new callback
    onAddBox: (String, String) -> Unit,    // new callback: categoryName, boxName
    onAddBoxContent: (String, String, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    var showAddBoxDialog by remember { mutableStateOf(false) }
    var newBoxName by remember { mutableStateOf("") }
    val newBoxContents = remember { mutableStateListOf<BoxContent>() }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (selectedCategory != null) {
            CategoryDetailScreen(
                category = selectedCategory!!,
                onBack = { selectedCategory = null },
                allBoxes = allBoxes,
                onBoxClick = onBoxClick
            )
        } else {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                categories.forEach { category ->
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(8.dp).clickable { selectedCategory = category },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (selectedCategory != null) {
                    showAddBoxDialog = true
                    newBoxName = ""
                    newBoxContents.clear()
                } else {
                    showDialog = true
                }
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Text("+", fontWeight = FontWeight.Bold) }

        if (showAddBoxDialog) {
            AddBoxDialog(
                showDialog = showAddBoxDialog,
                onDismiss = { showAddBoxDialog = false },
                onAdd = { box ->
                    // Persist box first
                    onAddBox(selectedCategory!!.name, box.name)

                    // Persist the contents immediately
                    box.contents.forEach { content ->
                        onAddBoxContent(box.name, content.name, content.quantity)
                    }

                    // Add to local state so UI updates immediately
                    selectedCategory?.boxes?.add(box)
                    allBoxes.add(0, box)

                    showAddBoxDialog = false
                }
            )

        }

        if (showDialog) {
            AddCategoryDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onAdd = { name ->
                    categories.add(Category(name))
                    onAddCategory(name)  // save to Room
                    showDialog = false
                }
            )
        }
    }
}
