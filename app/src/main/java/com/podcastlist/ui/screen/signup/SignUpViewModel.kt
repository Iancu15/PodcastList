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

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT)
            return
        }

        if (!password.isValidPassword()) {
            snackbarManager.showMessage(INVALID_PASSWORD_TEXT)
            return
        }

        if (password != uiState.value.repeatPassword) {
            snackbarManager.showMessage(PASSWORDS_NOT_MATCH_TEXT)
            return
        }

        viewModelScope.launch {
            accountService.linkAccount(email, password) }
    }
}