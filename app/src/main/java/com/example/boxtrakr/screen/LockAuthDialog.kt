package com.example.boxtrakr.screen

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.boxtrakr.domain.Box
import com.example.boxtrakr.R

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

    // Store string resources in local variables
    val incorrectPasswordMsg = stringResource(R.string.incorrect_password)
    val hidePasswordMsg = stringResource(R.string.hide_password)
    val showPasswordMsg = stringResource(R.string.show_password)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (box.password == enteredPassword) {
                    onSuccess()
                } else {
                    error = incorrectPasswordMsg
                }
            }) {
                Text(stringResource(R.string.unlock))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.enter_password)) },
        text = {
            Column {
                OutlinedTextField(
                    value = enteredPassword,
                    onValueChange = { enteredPassword = it },
                    label = { Text(stringResource(R.string.password)) },
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
                                    hidePasswordMsg
                                else
                                    showPasswordMsg
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
                    Text(stringResource(R.string.use_fingerprint))
                }
            }
        }
    )
}