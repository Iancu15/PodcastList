package com.podcastlist

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.podcastlist.auth.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    fun isUserLoggedOut(): Boolean {
        return accountService.isUserLoggedOut()
    }

    fun getUserEmail(): String? {
        return accountService.getUserEmail()
    }

    fun signOutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            accountService.signOut()
        }
    }

}