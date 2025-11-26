package com.example.boxtrakr.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.Category
import com.example.boxtrakr.domain.BoxContent

@Composable
fun CategoryDetailScreen(
    category: Category,
    onBack: () -> Unit,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Back",
            modifier = Modifier
                .clickable { onBack() }
                .padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text("Category: ${category.name}", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        category.boxes.forEach { box ->
            Text(
                "-${box.name}",
                modifier = Modifier.clickable { onBoxClick(box) },
                fontWeight = FontWeight.Medium
            )
            box.contents.forEach { content ->
                Text("- ${content.name} x${content.quantity}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
