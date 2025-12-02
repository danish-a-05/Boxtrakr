package com.example.boxtrakr.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.boxtrakr.domain.Category
import java.io.File
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: Category,
    onBack: () -> Unit,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit
) {
    var categoryState by remember { mutableStateOf(category) }
    var allBoxesState by remember { mutableStateOf(allBoxes.toMutableList()) }

    LaunchedEffect(category, allBoxes) {
        categoryState = category
        allBoxesState = allBoxes.toMutableList()
    }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = categoryState.name,
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
            if (categoryState.boxes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No boxes in this category.")
                }
            } else {
                categoryState.boxes.forEach { box ->
                    SwipeToDeleteCategoryBoxItem(
                        box = box,
                        category = categoryState,
                        allBoxes = allBoxesState,
                        onBoxClick = onBoxClick,
                        onBoxRemoved = {
                            // Update the states to trigger recomposition
                            val newCategoryBoxes = categoryState.boxes.toMutableList().apply { remove(box) }
                            val newCategory = Category(categoryState.name, newCategoryBoxes)
                            categoryState = newCategory

                            val newAllBoxes = allBoxesState.toMutableList().apply { remove(box) }
                            allBoxesState = newAllBoxes

                            // Also update the original lists
                            category.boxes.remove(box)
                            allBoxes.remove(box)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteCategoryBoxItem(
    box: Box,
    category: Category,
    allBoxes: MutableList<Box>,
    onBoxClick: (Box) -> Unit,
    onBoxRemoved: () -> Unit
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
                                onBoxRemoved()
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
                            contentDescription = "Box thumbnail",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = box.name,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (box.isPrivate) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Private box — locked",
                            style = MaterialTheme.typography.bodySmall
                        )
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