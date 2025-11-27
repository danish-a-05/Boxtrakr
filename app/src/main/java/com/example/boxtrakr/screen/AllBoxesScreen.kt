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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBoxesScreen(
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Text("All Boxes", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search boxes") },
                    singleLine = true
                )
            }
        }
    ) { inner ->
        val filteredBoxes = if (searchText.isBlank()) allBoxes else allBoxes.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        if (filteredBoxes.isEmpty()) {
            Text(
                "No boxes available.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                Modifier
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBoxes) { box ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBoxClick(box) }
                    ) {
                        Text(
                            text = box.name,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
