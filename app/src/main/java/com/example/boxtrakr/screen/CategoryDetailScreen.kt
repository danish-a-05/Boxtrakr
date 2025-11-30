package com.example.boxtrakr.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: Category,
    onBack: () -> Unit,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (category.boxes.isEmpty()) {
                Text("No boxes in this category.")
            } else {
                category.boxes.forEach { box ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBoxClick(box) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // Box name
                            Text(
                                text = box.name,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // subtext when box is private
                            if (box.isPrivate) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Private box — locked",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
