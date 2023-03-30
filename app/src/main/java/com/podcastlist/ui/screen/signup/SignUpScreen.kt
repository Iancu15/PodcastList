package com.podcastlist.ui.screen.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.R
import com.podcastlist.auth.AuthButton
import com.podcastlist.auth.EmailField
import com.podcastlist.auth.PasswordField
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.composables.PodcastIcon
import com.podcastlist.ui.screen.login.LoginViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    navigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState
    viewModel.snackbarManager = snackbarManager

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PodcastIcon(modifier = Modifier
            .size(200.dp)
            .padding(bottom = 20.dp))
        EmailField(value = uiState.email, onEmailChange = viewModel::onEmailChange)
        PasswordField(value = uiState.password, onPasswordChange = viewModel::onPasswordChange)
        PasswordField(value = uiState.repeatPassword, onPasswordChange = viewModel::onRepeatPasswordChange)
        AuthButton(stringResource(R.string.signup_title)) {
            viewModel.onSignUpClick(navigateToLogin)
        }
    }
}