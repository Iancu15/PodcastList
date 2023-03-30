package com.podcastlist.ui.screen.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.podcastlist.auth.*
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun onSignInClick(navigateHome: () -> Unit) {
        if (!email.isEmailValid()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT, true)
            return
        }

        if (!password.isPasswordValid()) {
            snackbarManager.showMessage(INVALID_PASSWORD_TEXT, true)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountService.authenticate(email, password)
                withContext(Dispatchers.Main) {
                    navigateHome()
                }
            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                        snackbarManager.showMessage(INVALID_COMBINATION_TEXT, true)
                    }
                    is FirebaseAuthException -> e.message?.let { snackbarManager.showMessage(it, true) }
                    else -> throw e
                }
            }
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isEmailValid()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT, true)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            accountService.sendRecoveryEmail(email)
            snackbarManager.showMessage(RECOVERY_MAIL_TEXT, true)
        }
    }

}
