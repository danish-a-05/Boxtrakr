package com.example.boxtrakr.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.domain.BoxContent
// for camera composable
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.boxtrakr.R

@Composable
fun AddBoxDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAdd: (Box) -> Unit
) {
    var newBoxName by remember { mutableStateOf("") }
    val newBoxContents = remember { mutableStateListOf<BoxContent>() }
    var contentName by remember { mutableStateOf("") }
    var contentQuantity by remember { mutableStateOf("") }

    // Private box toggle and password
    var isPrivate by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Store the password error message in a local variable
    val passwordErrorMsg = stringResource(R.string.password_error)

    // launcher to start CameraActivity and get returned image path
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra("resultImagePath")
            // After camera returns with imagePath (user kept the photo), create the box and call onAdd
            val newBox = Box(
                name = newBoxName,
                contents = newBoxContents.toMutableList(),
                isPrivate = isPrivate,
                password = if (isPrivate) password else null,
                imagePath = imagePath
            )

            // Clear UI state
            newBoxName = ""
            newBoxContents.clear()
            isPrivate = false
            password = ""
            passwordError = ""

            onAdd(newBox)
        } else {
            // user cancelled camera — create box without image (still persist values)
            val newBox = Box(
                name = newBoxName,
                contents = newBoxContents.toMutableList(),
                isPrivate = isPrivate,
                password = if (isPrivate) password else null,
                imagePath = null
            )

            // Clear UI state
            newBoxName = ""
            newBoxContents.clear()
            isPrivate = false
            password = ""
            passwordError = ""

            onAdd(newBox)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.add_new_box)) },
            text = {
                Column {
                    Text(stringResource(R.string.box_name))
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = newBoxName,
                        onValueChange = { newBoxName = it },
                        placeholder = { Text(stringResource(R.string.box_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.add_content), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column {
                        TextField(
                            value = contentName,
                            onValueChange = { contentName = it },
                            placeholder = { Text(stringResource(R.string.content_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            TextField(
                                value = contentQuantity,
                                onValueChange = { contentQuantity = it },
                                placeholder = { Text(stringResource(R.string.quantity)) },
                                modifier = Modifier.width(110.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = {
                                if (contentName.isNotBlank() && contentQuantity.toIntOrNull() != null) {
                                    newBoxContents.add(BoxContent(contentName, contentQuantity.toInt()))
                                    contentName = ""
                                    contentQuantity = ""
                                }
                            }) {
                                Text(stringResource(R.string.add_content_button))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Private toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.private_box))
                        Switch(checked = isPrivate, onCheckedChange = {
                            isPrivate = it
                            if (!it) {
                                // clear password when toggling off
                                password = ""
                                passwordError = ""
                            }
                        })
                    }

                    // Password input when private
                    if (isPrivate) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = ""
                            },
                            label = { Text(stringResource(R.string.password_hint)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (passwordError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(passwordError, color = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    newBoxContents.forEach { c ->
                        Text("- ${c.name} x${c.quantity}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Validation
                    if (newBoxName.isBlank()) return@TextButton

                    if (isPrivate) {
                        if (password.length < 6) {
                            passwordError = passwordErrorMsg
                            return@TextButton
                        }
                    }

                    val intent = Intent(context, CameraActivity::class.java)
                    cameraLauncher.launch(intent)

                }) {
                    Text(stringResource(R.string.finish), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}