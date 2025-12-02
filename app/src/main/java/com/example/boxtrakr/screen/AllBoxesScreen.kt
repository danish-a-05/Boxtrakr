package com.example.boxtrakr.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import java.io.File
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBoxesScreen(
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var boxesState by remember { mutableStateOf(allBoxes.toMutableList()) }

    LaunchedEffect(allBoxes) {
        boxesState = allBoxes.toMutableList()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
        val filteredBoxes = if (searchText.isBlank()) boxesState else boxesState.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        if (filteredBoxes.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No boxes available.")
            }
        } else {
            LazyColumn(
                Modifier
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBoxes.size, key = { filteredBoxes[it].name }) { index ->
                    val box = filteredBoxes[index]
                    SwipeToDeleteBoxItem(
                        box = box,
                        onBoxClick = onBoxClick,
                        onDelete = {
                            // Create new list to trigger recomposition
                            val newList = boxesState.toMutableList().apply { remove(box) }
                            boxesState = newList
                            allBoxes.remove(box)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteBoxItem(
    box: Box,
    onBoxClick: (Box) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var itemWidth by remember { mutableStateOf(0f) }
    val swipeThreshold = 0.5f // 50% of item width
    var isBeingDeleted by remember { mutableStateOf(false) }

    if (isBeingDeleted) {
        // Don't render anything if being deleted
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Delete background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red, RoundedCornerShape(8.dp))
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Draggable item
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // Check if swipe passed threshold
                            if (offsetX < -itemWidth * swipeThreshold && itemWidth > 0) {
                                // Swiped enough to delete
                                isBeingDeleted = true
                                onDelete()
                            } else {
                                // Reset position
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            // Only allow left swipe (negative drag)
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceAtMost(0f) // Can't drag right, only left
                            change.consume()
                        }
                    )
                }
                .onSizeChanged {
                    itemWidth = it.width.toFloat()
                }
                .clickable {
                    // Only trigger click if not swiped
                    if (offsetX == 0f) {
                        onBoxClick(box)
                    } else {
                        // Reset if clicked while swiped
                        offsetX = 0f
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!box.isPrivate && box.imagePath != null) {
                    val bmp = remember(box.imagePath) {
                        val f = File(box.imagePath)
                        if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
                    }
                    if (bmp != null) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "thumbnail",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = box.name,
                        fontWeight = FontWeight.Bold
                    )
                    if (box.isPrivate) {
                        Spacer(Modifier.height(4.dp))
                        Text("Private box — locked", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Swipe hint
                if (offsetX == 0f) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Swipe to delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}