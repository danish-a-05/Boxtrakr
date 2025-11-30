package com.example.boxtrakr.screen

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.boxtrakr.domain.Box

@Composable
fun LockAuthDialog(
    box: Box,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onFingerprintRequest: () -> Unit
) {
    var enteredPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (box.password == enteredPassword) {
                    onSuccess()
                } else {
                    error = "Incorrect password"
                }
            }) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Enter Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = enteredPassword,
                    onValueChange = { enteredPassword = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword)
                                    Icons.Filled.VisibilityOff
                                else
                                    Icons.Filled.Visibility,
                                contentDescription = if (showPassword)
                                    "Hide password"
                                else
                                    "Show password"
                            )
                        }
                    }
                )

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }

                // Button to use fingerprint instead
                TextButton(
                    onClick = onFingerprintRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Use fingerprint instead")
                }
            }
        }
    )
}
