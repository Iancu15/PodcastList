package com.podcastlist.ui.screen.edit_account

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

data class EditAccountUIState(
    val email: String = "",
    val newPassword: String = "",
    val newPasswordRepeat: String = "",
)

@HiltViewModel
class EditAccountViewModel @Inject constructor(
    private val accountService: AccountService
) : PodcastListViewModel() {
    var uiState = mutableStateOf(EditAccountUIState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.newPassword
    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(newPassword = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(newPasswordRepeat = newValue)
    }

    fun changeEmail() {
        if (!email.isEmailValid()) {
            snackbarManager.showMessage(INVALID_EMAIL_TEXT, true)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountService.changeEmail(email)
                snackbarManager.showMessage(SUCCESSFUL_EMAIL_CHANGE, true)
            } catch (e: FirebaseAuthException) {
                e.message?.let { snackbarManager.showMessage(it, true) }
            }
        }
    }

    fun changePassword() {
        if (!password.isPasswordValid()) {
            snackbarManager.showMessage(INVALID_PASSWORD_TEXT, true)
            return
        }

        if (password != uiState.value.newPasswordRepeat) {
            snackbarManager.showMessage(PASSWORDS_NOT_MATCH_TEXT, true)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountService.changePassword(password)
                snackbarManager.showMessage(SUCCESSFUL_PASSWORD_CHANGE, true)
            } catch (e: FirebaseAuthException) {
                e.message?.let { snackbarManager.showMessage(it, true) }
            }
        }
    }

    fun deleteAccount(navigateToHome: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountService.deleteAccount()
                snackbarManager.showMessage(SUCCESSFUL_ACCOUNT_DELETE, true)
                withContext(Dispatchers.Main) {
                    navigateToHome()
                }
            } catch (e: FirebaseAuthException) {
                e.message?.let { snackbarManager.showMessage(it, true) }
            }
        }
    }
}