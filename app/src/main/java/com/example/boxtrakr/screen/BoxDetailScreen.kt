package com.example.boxtrakr.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.BoxContent
import java.io.File
import com.example.boxtrakr.R

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
                    Text(stringResource(R.string.back))
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // IMAGE SECTION (correctly wrapped in item)
            item {
                if (box.imagePath != null) {
                    val bitmap = remember(box.imagePath) {
                        val file = File(box.imagePath)
                        if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                    }

                    if (bitmap != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = stringResource(R.string.box_image),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            items(box.contents) { item ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.name, fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.quantity_full) + ": ${item.quantity}")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.add_item)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(stringResource(R.string.item_name)) }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newQty,
                        onValueChange = { newQty = it },
                        label = { Text(stringResource(R.string.quantity_full)) }
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
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}