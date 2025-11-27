package com.example.boxtrakr.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box

@Composable
fun AllBoxesScreen(
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar at the top
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search boxes") }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                            .clickable { onBoxClick(box) }
                    ) {
                        Text(box.name, fontWeight = FontWeight.Bold)
                        box.contents.forEach { c ->
                            Text("- ${c.name} x${c.quantity}")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
