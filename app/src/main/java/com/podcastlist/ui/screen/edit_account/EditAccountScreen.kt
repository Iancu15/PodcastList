package com.podcastlist.ui.screen.edit_account

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.text.Normalizer.Form

@Composable
fun EditAccountScreen(
    viewModel: EditAccountViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState
    viewModel.snackbarManager = snackbarManager

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
            viewModel.deleteAccount(navigateToHome)
        }
    }
}