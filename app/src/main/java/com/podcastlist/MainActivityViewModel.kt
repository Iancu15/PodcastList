package com.podcastlist

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.auth.AccountService
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountService: AccountService,
    private val authorizationService: AuthorizationService
) : PodcastListViewModel() {

    fun isUserLoggedOut(): Boolean {
        return accountService.isUserLoggedOut()
    }

    fun getUserEmail(): String? {
        return accountService.getUserEmail()
    }

    fun signOutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                accountService.signOut()
            }
        }
    }

    fun storeAuthorizationToken(accessToken: String, expiresIn: Int) {
        authorizationService.authorizationToken = "Authorization: Bearer $accessToken"
        authorizationService.isTokenAvailable = true
        Log.d("MainActivityViewModel", "Token expires in $expiresIn seconds")
        Log.d("MainActivityViewModel", "Token: ${authorizationService.authorizationToken}")
    }

}