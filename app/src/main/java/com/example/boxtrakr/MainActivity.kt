package com.example.boxtrakr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.example.boxtrakr.data.*
import com.example.boxtrakr.domain.*
import com.example.boxtrakr.model.*
import com.example.boxtrakr.screen.*
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
// for the fingerprint feature to work
import androidx.fragment.app.FragmentActivity

// Add these imports for theme
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.example.boxtrakr.data.SettingsDataStore
import com.example.boxtrakr.ui.theme.ThemeSetting
import com.example.boxtrakr.ui.theme.BoxTrakrTheme
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.ui.platform.LocalView

class MainActivity : FragmentActivity() {

    // ROOM DATABASE SETUP
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "boxtrakr.db"
        )
            // destructive fallback on schema change
            .fallbackToDestructiveMigration()
            .build()
    }

    // VIEWMODELS
    private val categoryVm: CategoryViewModel by viewModels { CategoryViewModel.factory(db) }
    private val boxVm: BoxViewModel by viewModels { BoxViewModel.factory(db) }
    private val boxContentVm: BoxContentViewModel by viewModels { BoxContentViewModel.factory(db) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DataStore
        val settingsDataStore = SettingsDataStore(applicationContext)

        setContent {
            // Collect theme setting from DataStore
            val themeSettingState by produceState<ThemeSetting>(
                initialValue = ThemeSetting.SYSTEM,
                producer = {
                    lifecycleScope.launch {
                        settingsDataStore.themeSetting.collectLatest { theme ->
                            value = theme
                        }
                    }
                }
            )

            // Apply edge-to-edge BEFORE setting content
            val view = LocalView.current
            if (!view.isInEditMode) {
                val window = (view.context as android.app.Activity).window
                WindowCompat.setDecorFitsSystemWindows(window, false)
            }

            val scope = rememberCoroutineScope()

            // Collect Room flows
            val categoryEntities by categoryVm.categories.collectAsState(initial = emptyList())
            val boxEntities by boxVm.boxes.collectAsState(initial = emptyList())
            val contentEntities by boxContentVm.boxContents.collectAsState(initial = emptyList())

            // Map Room entities to domain objects
            val categories = remember(categoryEntities, boxEntities, contentEntities) {
                mapToDomain(categoryEntities, boxEntities, contentEntities)
            }

            // Reactive list of all boxes (domain)
            val allBoxes = remember { mutableStateListOf<Box>() }
            LaunchedEffect(categories) {
                allBoxes.clear()
                allBoxes.addAll(categories.flatMap { it.boxes })
            }

            var selectedBox by remember { mutableStateOf<Box?>(null) }

            // Add theme setting state
            var showThemeSettings by remember { mutableStateOf(false) }

            // NEW: For password dialog flow when opening a private box
            var pendingBoxToOpen by remember { mutableStateOf<Box?>(null) }
            var showAuthDialog by remember { mutableStateOf(false) }

            // Functions to dynamically add new items
            fun addCategory(catName: String) {
                scope.launch { categoryVm.addCategory(catName) }
            }

            // Accept a domain Box and persist the privacy fields to DB
            fun addBox(categoryName: String, box: Box) {
                scope.launch {
                    // insert box entity (name, categoryName, isPrivate, password, imagePath)
                    boxVm.addBox(box.name, categoryName, box.isPrivate, box.password, box.imagePath)
                }
            }

            fun addBoxContent(boxName: String, contentName: String, quantity: Int) {
                scope.launch { boxContentVm.addBoxContent(contentName, quantity, boxName) }
            }

            // Wrap everything in your theme
            BoxTrakrTheme(themeSetting = themeSettingState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                        // If a box is selected and authenticated, show its details
                        if (selectedBox != null) {
                            BoxDetailScreen(
                                box = selectedBox!!,
                                onBack = { selectedBox = null },
                                onAddContent = { name, qty -> addBoxContent(selectedBox!!.name, name, qty) }
                            )
                            return@Column
                        }

                        // When auth dialog (password) is shown, use your LockAuthDialog composable
                        if (showAuthDialog && pendingBoxToOpen != null) {
                            LockAuthDialog(
                                box = pendingBoxToOpen!!,
                                onDismiss = {
                                    showAuthDialog = false
                                    pendingBoxToOpen = null
                                },
                                onSuccess = {
                                    // Auth succeeded -> open the pending box
                                    showAuthDialog = false
                                    selectedBox = pendingBoxToOpen
                                    pendingBoxToOpen = null
                                },
                                onFingerprintRequest = {
                                    showAuthDialog = false
                                    launchFingerprintAuth(
                                        onSuccess = {
                                            selectedBox = pendingBoxToOpen
                                            pendingBoxToOpen = null
                                        }
                                    )
                                }
                            )
                        }

                        // Theme settings dialog
                        if (showThemeSettings) {
                            AlertDialog(
                                onDismissRequest = { showThemeSettings = false },
                                title = { Text("Theme Settings") },
                                text = {
                                    Column {
                                        ThemeSetting.entries.forEach { theme ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        scope.launch {
                                                            settingsDataStore.saveThemeSetting(theme)
                                                        }
                                                    }
                                                    .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = themeSettingState == theme,
                                                    onClick = {
                                                        scope.launch {
                                                            settingsDataStore.saveThemeSetting(theme)
                                                        }
                                                    }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = when (theme) {
                                                        ThemeSetting.LIGHT -> "Light"
                                                        ThemeSetting.DARK -> "Dark"
                                                        ThemeSetting.SYSTEM -> "System Default"
                                                    }
                                                )
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = { showThemeSettings = false }
                                    ) {
                                        Text("Close")
                                    }
                                }
                            )
                        }

                        // Search bar and tabs
                        var searchText by remember { mutableStateOf("") }
                        var selectedTab by remember { mutableStateOf(0) }
                        val tabTitles = listOf("All Boxes", "Categories")

                        // Add theme toggle button in the top row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TabsComponent(
                                tabTitles = tabTitles,
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )

                            // Use a conditional icon based on current theme
                            val themeIcon = when (themeSettingState) {
                                ThemeSetting.LIGHT -> Icons.Filled.LightMode
                                ThemeSetting.DARK -> Icons.Filled.DarkMode
                                ThemeSetting.SYSTEM -> Icons.Filled.BrightnessAuto
                            }

                            IconButton(onClick = { showThemeSettings = true }) {
                                Icon(
                                    imageVector = themeIcon,
                                    contentDescription = "Theme settings",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedTab) {
                            // All boxes screen: when click -> check if private; if so prompt; else open
                            0 -> AllBoxesScreen(
                                allBoxes = allBoxes,
                                onBoxClick = { box ->
                                    // find matching BoxEntity to check isPrivate (guaranteed by mapping)
                                    val matchingEntity = boxEntities.firstOrNull { it.name == box.name }
                                    if (matchingEntity?.isPrivate == true) {
                                        // show password prompt using domain Box (box contains password in domain mapping)
                                        pendingBoxToOpen = box
                                        showAuthDialog = true
                                    } else {
                                        selectedBox = box
                                    }
                                }
                            )

                            // Categories screen: pass callbacks (we changed onAddBox to accept (category, Box))
                            1 -> CategoriesScreen(
                                categories = categories,
                                allBoxes = allBoxes,
                                onBoxClick = { box ->
                                    val matchingEntity = boxEntities.firstOrNull { it.name == box.name }
                                    if (matchingEntity?.isPrivate == true) {
                                        pendingBoxToOpen = box
                                        showAuthDialog = true
                                    } else {
                                        selectedBox = box
                                    }
                                },
                                onAddCategory = { name -> addCategory(name) },
                                onAddBox = { catName, box -> addBox(catName, box) },
                                onAddBoxContent = { boxName, contentName, qty -> addBoxContent(boxName, contentName, qty) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Sample default data if DB is empty (keeps your original sample)
    private val sampleCategories = mutableStateListOf(
        Category("Work", mutableListOf(Box("Project A"), Box("Project B"))),
        Category("Personal", mutableListOf(Box("Shoes Box"), Box("Old Tech")))
    )

    // Helper to map entities -> domain classes (now include password/isPrivate)
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

                // include isPrivate and password in domain Box
                cat.boxes.add(
                    Box(
                        name = boxEntity.name,
                        contents = boxContents,
                        isPrivate = boxEntity.isPrivate,
                        password = boxEntity.password,
                        imagePath = boxEntity.imagePath
                    )
                )
            }
        }

        return categoryMap.values.toMutableList().ifEmpty { sampleCategories }
    }
}

fun FragmentActivity.launchFingerprintAuth(
    onSuccess: () -> Unit,
    onError: (String) -> Unit = {},
    onFail: () -> Unit = {}
) {
    val executor = ContextCompat.getMainExecutor(this)

    val biometricPrompt = BiometricPrompt(
        this,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFail()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock with Fingerprint")
        .setSubtitle("Authenticate to access this box")
        .setNegativeButtonText("Cancel")
        .setConfirmationRequired(false)
        .build()

    biometricPrompt.authenticate(promptInfo)
}