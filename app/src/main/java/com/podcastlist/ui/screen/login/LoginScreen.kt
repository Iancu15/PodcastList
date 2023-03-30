package com.podcastlist.ui.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcastlist.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.podcastlist.R
import com.podcastlist.auth.AuthButton
import com.podcastlist.auth.EmailField
import com.podcastlist.auth.PasswordField
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.composables.FormColumn
import com.podcastlist.ui.composables.PodcastIcon
import kotlinx.coroutines.Dispatchers

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    navigateHome: () -> Unit,
    navigateRegister: () -> Unit
) {
    val uiState by viewModel.uiState
    viewModel.snackbarManager = snackbarManager

    FormColumn {
        PodcastIcon(modifier = Modifier
            .size(200.dp)
            .padding(bottom = 20.dp))
        EmailField(value = uiState.email, onEmailChange = viewModel::onEmailChange)
        PasswordField(value = uiState.password, onPasswordChange = viewModel::onPasswordChange)
        AuthButton(stringResource(id = R.string.login_title)) {
            viewModel.onSignInClick(navigateHome)
        }

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = {
                    viewModel.onForgotPasswordClick()
                }
            ) {
                Text(stringResource(R.string.forgot_password_text))
            }

            Divider(
                modifier = Modifier
                    .padding(top = 18.dp)
                    .height(13.dp)
                    .width(1.dp),
                color = MaterialTheme.colors.primary
            )

            TextButton(
                onClick = {
                    navigateRegister()
                }
            ) {
                Text(stringResource(R.string.register_text))
            }
        }
    }
}

@Composable
fun LoginDrawerItem(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentScreen: Screen,
    onNavigate: () -> Unit
) {
    val surfaceColor = if (currentScreen == Screen.LOGIN) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface

    Surface(
        color = surfaceColor,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clickable {
                onNavigate()
                scope.launch(Dispatchers.Main) { drawerState.close() }
            }
            .fillMaxWidth()
            .height(60.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.h6,
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}