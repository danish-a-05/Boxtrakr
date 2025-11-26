package com.example.boxtrakr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.boxtrakr.domain.*
import com.example.boxtrakr.screen.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val categories = remember { sampleCategories }
            val allBoxes = remember {
                mutableStateListOf<Box>().apply {
                    addAll(categories.flatMap { it.boxes })
                }
            }

            var selectedBox by remember { mutableStateOf<Box?>(null) }

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {

                if (selectedBox != null) {
                    BoxDetailScreen(
                        box = selectedBox!!,
                        onBack = { selectedBox = null }
                    )
                    return@Column
                }

                // Search bar and tabs can stay here or move into screens as needed

                var searchText by remember { mutableStateOf("") }
                var selectedTab by remember { mutableStateOf(0) }
                val tabTitles = listOf("All Boxes", "Categories")

                TabsComponent(
                    tabTitles = tabTitles,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> AllBoxesScreen(
                        searchText = searchText,
                        allBoxes = allBoxes,
                        onBoxClick = { box -> selectedBox = box }
                    )
                    1 -> CategoriesScreen(
                        categories = categories,
                        allBoxes = allBoxes,
                        onBoxClick = { box -> selectedBox = box }
                    )
                }
            }
        }
    }

    private val sampleCategories = mutableStateListOf(
        Category("Work", mutableListOf(Box("Project A"), Box("Project B"))),
        Category("Personal", mutableListOf(Box("Shoes Box"), Box("Old Tech")))
    )
}
