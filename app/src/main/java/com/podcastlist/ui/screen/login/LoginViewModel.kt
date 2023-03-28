package com.podcastlist.ui.screen.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.podcastlist.auth.*
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = ""
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService)
    : PodcastListViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password
    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick() {
        if (!email.isValidEmail()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT)
            return
        }

        if (password.isValidPassword()) {
            snackbarManager.showMessage(INVALID_PASSWORD_TEXT)
            return
        }

        viewModelScope.launch {
            try {
                accountService.authenticate(email, password)
            } catch (e: FirebaseAuthInvalidUserException) {
                snackbarManager.showMessage(INVALID_COMBINATION_TEXT)
            }
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT)
            return
        }

        viewModelScope.launch {
            accountService.sendRecoveryEmail(email)
            snackbarManager.showMessage(RECOVERY_MAIL_TEXT)
        }
    }
}
