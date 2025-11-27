package com.example.boxtrakr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.example.boxtrakr.domain.*
import com.example.boxtrakr.screen.*
import com.example.boxtrakr.data.AppDatabase
import com.example.boxtrakr.model.*
import androidx.compose.runtime.collectAsState
import com.example.boxtrakr.data.BoxContentEntity
import com.example.boxtrakr.data.BoxEntity
import com.example.boxtrakr.data.CategoryEntity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // ROOM DATABASE SETUP
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "boxtrakr.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // VIEWMODELS
    private val categoryVm: CategoryViewModel by viewModels { CategoryViewModel.factory(db) }
    private val boxVm: BoxViewModel by viewModels { BoxViewModel.factory(db) }
    private val boxContentVm: BoxContentViewModel by viewModels { BoxContentViewModel.factory(db) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            val scope = rememberCoroutineScope()

            // Collect Room flows
            val categoryEntities by categoryVm.categories.collectAsState(initial = emptyList())
            val boxEntities by boxVm.boxes.collectAsState(initial = emptyList())
            val contentEntities by boxContentVm.boxContents.collectAsState(initial = emptyList())

            // Map Room entities to domain objects
            val categories = remember(categoryEntities, boxEntities, contentEntities) {
                mapToDomain(categoryEntities, boxEntities, contentEntities)
            }

            // Reactive list of all boxes
            val allBoxes = remember { mutableStateListOf<Box>() }
            LaunchedEffect(categories) {
                allBoxes.clear()
                allBoxes.addAll(categories.flatMap { it.boxes })
            }

            var selectedBox by remember { mutableStateOf<Box?>(null) }

            // Functions to dynamically add new items
            fun addCategory(catName: String) {
                scope.launch {
                    categoryVm.addCategory(catName)
                }
            }

            fun addBox(catName: String, boxName: String) {
                scope.launch {
                    boxVm.addBox(boxName, catName)
                }
            }

            fun addBoxContent(boxName: String, contentName: String, quantity: Int) {
                scope.launch {
                    boxContentVm.addBoxContent(contentName, quantity, boxName)
                }
            }

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {

                if (selectedBox != null) {
                    BoxDetailScreen(
                        box = selectedBox!!,
                        onBack = { selectedBox = null },
                        onAddContent = { name, qty -> addBoxContent(selectedBox!!.name, name, qty) }
                    )
                    return@Column
                }

                // Search bar and tabs
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
                        allBoxes = allBoxes,
                        onBoxClick = { box -> selectedBox = box }
                    )
                    1 -> CategoriesScreen(
                        categories = categories,
                        allBoxes = allBoxes,
                        onBoxClick = { box -> selectedBox = box },
                        onAddCategory = { name -> addCategory(name) },
                        onAddBox = { catName, boxName -> addBox(catName, boxName) },
                        onAddBoxContent = { boxName, contentName, qty -> addBoxContent(boxName, contentName, qty) }
                    )
                }
            }
        }
    }

    // Sample default data if DB is empty
    private val sampleCategories = mutableStateListOf(
        Category("Work", mutableListOf(Box("Project A"), Box("Project B"))),
        Category("Personal", mutableListOf(Box("Shoes Box"), Box("Old Tech")))
    )

    // Helper function to map Room entities to domain classes
    private fun mapToDomain(
        categories: List<CategoryEntity>,
        boxes: List<BoxEntity>,
        contents: List<BoxContentEntity>
    ): MutableList<Category> {
        val categoryMap = categories.associateBy { it.name }.mapValues { (_, cat) ->
            Category(cat.name, mutableListOf())
        }.toMutableMap()

        boxes.forEach { boxEntity ->
            val cat = categoryMap[boxEntity.categoryName]
            if (cat != null) {
                val boxContents = contents
                    .filter { it.boxName == boxEntity.name }
                    .map { BoxContent(it.name, it.quantity) }
                    .toMutableList()
                cat.boxes.add(Box(boxEntity.name, boxContents))
            }
        }

        return categoryMap.values.toMutableList().ifEmpty { sampleCategories }
    }
}
