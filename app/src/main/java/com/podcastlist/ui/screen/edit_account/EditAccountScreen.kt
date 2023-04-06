package com.podcastlist.ui.screen.edit_account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.R
import com.podcastlist.auth.AuthButton
import com.podcastlist.auth.EmailField
import com.podcastlist.auth.PasswordField
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.composables.FormColumn
import com.podcastlist.ui.composables.FormDivider

@Composable
fun EditAccountScreen(
    viewModel: EditAccountViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState
    viewModel.snackbarManager = snackbarManager
    var openDialog by remember { mutableStateOf(false) }

    FormColumn {
        EmailField(value = uiState.email, onEmailChange = viewModel::onEmailChange)
        AuthButton(stringResource(R.string.change_email_text)) {
            viewModel.changeEmail()
        }

        FormDivider()
        PasswordField(
            value = uiState.newPassword,
            label = stringResource(R.string.new_password_label),
            placeholder = stringResource(R.string.new_password_placeholder),
            onPasswordChange = viewModel::onPasswordChange
        )

        PasswordField(
            value = uiState.newPasswordRepeat,
            label = stringResource(R.string.new_password_label),
            placeholder = stringResource(R.string.new_password_placeholder),
            onPasswordChange = viewModel::onRepeatPasswordChange
        )

        AuthButton(stringResource(R.string.change_password_text)) {
            viewModel.changePassword()
        }

        FormDivider()
        AuthButton(
            buttonText = "Delete account",
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.error,
                contentColor = MaterialTheme.colors.onError
            )
        ) {
            openDialog = true
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                title = { Text("Are you sure you want to delete the account?") },
                text = {
                    Text("Beware that neither the account nor any its associated data can be recovered after deleting the account.")
                },
                buttons = {
                    Row (
                        modifier = Modifier.padding(bottom = 10.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                openDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                openDialog = false
                                viewModel.deleteAccount(navigateToHome)
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error,
                                contentColor = MaterialTheme.colors.onError
                            )
                        ) {
                            Text("Yes, delete it")
                        }
                    }
                }
            )
        }
    }
}