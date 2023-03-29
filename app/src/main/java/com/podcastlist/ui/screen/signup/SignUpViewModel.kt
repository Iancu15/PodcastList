package com.podcastlist.ui.screen.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastlist.auth.*
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    fun onSignUpClick() {
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

        viewModelScope.launch {
            accountService.linkAccount(email, password) }
    }
}