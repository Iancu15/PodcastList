package com.podcastlist.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcastlist.auth.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    fun onAppStart() {
        if (!accountService.hasUser)
            viewModelScope.launch(Dispatchers.IO) {
                accountService.createAnonymousAccount()
            }
    }

}