package com.example.boxtrakr

import android.R.attr.fontWeight
import android.R.attr.onClick
import android.R.attr.text
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val categories = remember {sampleCategories}
            val allBoxes = remember { mutableStateListOf<Box>().apply {
                addAll(categories.flatMap {it.boxes})
            }}

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {

                // Search bar
                var searchText by remember { mutableStateOf("") }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    if (searchText.isEmpty()) {
                        Text(
                            "Search for a box",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                var selectedTab by remember { mutableStateOf(0) }
                val tabTitles = listOf("All Boxes", "Categories")

                TabRow(selectedTabIndex = selectedTab) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab content
                when (selectedTab) {
                    0 -> AllBoxesTab(searchText, allBoxes)
                    1 -> CategoriesTab(categories, allBoxes)
                }
            }
        }
    }

    // Data model for boxes and categories
    data class Box(val name: String, val contents: MutableList<BoxContent> = mutableListOf())
    data class BoxContent(val name: String, val quantity: Int)
    data class Category(val name: String, val boxes: MutableList<Box> = mutableListOf())

    // sampleCategories for the list
    private val sampleCategories = mutableStateListOf(
        Category("Work", mutableListOf(Box("Project A"), Box("Project B"))),
        Category("Personal", mutableListOf(Box("Shoes Box"), Box("Old Tech")))
    )
    @Composable
    fun AllBoxesTab(searchText: String, allBoxes: MutableList<Box>) {

        val filteredBoxes = if (searchText.isBlank()) allBoxes else allBoxes.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        if (filteredBoxes.isEmpty()) {
            Text("No Boxes yet.", modifier = Modifier.padding(8.dp))
        } else {
            LazyColumn {
                items(filteredBoxes) { box ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(box.name, fontWeight = FontWeight.Bold)
                        if (box.contents.isNotEmpty()) {
                            box.contents.forEach { content ->
                                Text("- ${content.name} x${content.quantity}")
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    @Composable
    fun CategoriesTab(
        categories: MutableList<Category>, // shared list
        allBoxes: MutableList<Box>        // shared all boxes list
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var newCategory by remember { mutableStateOf("")}

        var selectedCategory by remember { mutableStateOf<Category?>(null)}

        var showAddBoxDialog by remember { mutableStateOf(false)}
        var newBoxName by remember { mutableStateOf("")}
        val newBoxContents = remember { mutableStateListOf<BoxContent>()}

        var contentName by remember { mutableStateOf("")}
        var contentQuantity by remember { mutableStateOf("")}

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            if (selectedCategory != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Back",
                        modifier = Modifier
                            .clickable { selectedCategory = null }
                            .padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Category: ${selectedCategory!!.name}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    selectedCategory!!.boxes.forEach { box ->
                        Text("- ${box.name}", fontWeight = FontWeight.Medium)
                        box.contents.forEach { content ->
                            Text("- ${content.name} x${content.quantity}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                // Categories list
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

            Button(
                onClick = {
                    if (selectedCategory != null) {
                        showAddBoxDialog = true
                        newBoxName = ""
                        newBoxContents.clear()
                    } else {
                        showDialog = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("+", fontWeight = FontWeight.Bold)
            }

            // Add new box dialog
            if (showAddBoxDialog) {
                AlertDialog(
                    onDismissRequest = { showAddBoxDialog = false },
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
                                selectedCategory?.boxes?.add(newBox)
                                allBoxes.add(0, newBox) // add to shared allBoxes
                                newBoxName = ""
                                newBoxContents.clear()
                            }
                            showAddBoxDialog = false
                        }) {
                            Text("Finish", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddBoxDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Add new category dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
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
                                categories.add(Category(newCategory)) // update shared list
                                newCategory = ""
                            }
                            showDialog = false
                        }) {
                            Text("Add", fontWeight = FontWeight.Bold)
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
    }
}