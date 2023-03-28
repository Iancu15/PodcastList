package com.podcastlist.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcastlist.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.podcastlist.R
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.login.LoginViewModel
@Composable
fun EmailField(
    value: String,
    onEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        singleLine = true,
        label = { Text(stringResource(R.string.email_label)) },
        onValueChange = { onEmailChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.email_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = stringResource(
            R.string.email_icon)
        ) }
    )
}

@Composable
fun PasswordField(
    value: String,
    onPasswordChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    val iconId =
        if (isVisible) R.drawable.visibility_48px else R.drawable.visibility_off_48px

    OutlinedTextField(
        singleLine = true,
        label = { Text(stringResource(R.string.password_label)) },
        onValueChange = { onPasswordChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.password_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(
            R.string.password_icon)
        ) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = stringResource(R.string.visibility_icon),
                    modifier = Modifier.size(30.dp).padding(bottom = 10.dp)
                )
            }
        },
        visualTransformation = visualTransformation
    )
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager
) {
    val uiState by viewModel.uiState
//    viewModel.setSnackbarManager(snackbarManager)
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(value = uiState.email, onEmailChange = viewModel::onEmailChange)
        PasswordField(value = uiState.password, onPasswordChange = viewModel::onPasswordChange)
        Button(
            onClick = {
                //viewModel.onSignInClick()
                keyboardController?.hide()
            }
        ) {
            Text("Login")
        }
        
        TextButton(
            onClick = {
                //viewModel.onForgotPasswordClick()
                keyboardController?.hide()
            }
        ) {
            Text("Forgot password")
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
                scope.launch { drawerState.close() }
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