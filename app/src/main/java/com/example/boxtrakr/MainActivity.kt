package com.example.boxtrakr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
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
                    0 -> AllBoxesTab()
                    1 -> CategoriesTab()
                }
            }
        }
    }

    @Composable
    fun AllBoxesTab() {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Your Boxes will appear here", modifier = Modifier.padding(8.dp))
            // Later: add a list of box cards
        }
    }

    @Composable
    fun CategoriesTab() {
        var showDialog by remember { mutableStateOf(false) }
        var newCategory by remember { mutableStateOf("")}

        Box( modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(onClick = { /* TODO: Add category */ }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), shape = RoundedCornerShape(15.dp)) {
                Text("+", fontWeight = FontWeight.Bold)
            }

            //Category Dialog for the add button
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false},
                    title = {Text("Add New Category")},
                    text = {
                        Column {
                            Text("Enter category name:")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = newCategory,
                                onValueChange = { newCategory = it },
                                placeholder = { Text("Category name")}
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (newCategory.isNotBlank()) {
                                categories.add(newCategory)
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
