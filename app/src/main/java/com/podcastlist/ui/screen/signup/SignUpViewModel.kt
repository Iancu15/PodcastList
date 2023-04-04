package com.podcastlist.ui.screen.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.podcastlist.auth.*
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
) : PodcastListViewModel() {
    var uiState = mutableStateOf(SignUpUiState())
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

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(navigateToLogin: () -> Unit) {
        if (!email.isEmailValid()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT, true)
            return
        }

        if (!password.isPasswordValid()) {
            snackbarManager.showMessage(INVALID_PASSWORD_TEXT, true)
            return
        }

        if (password != uiState.value.repeatPassword) {
            snackbarManager.showMessage(PASSWORDS_NOT_MATCH_TEXT, true)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountService.linkAccount(email, password)
                snackbarManager.showMessage(SUCCESSFUL_REGISTER, true)
                withContext(Dispatchers.Main) {
                    navigateToLogin()
                }
            } catch (e: FirebaseAuthException) {
                e.message?.let { snackbarManager.showMessage(it, true) }
            }
        }
    }
}