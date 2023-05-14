package com.podcastlist

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.auth.AccountService
import com.podcastlist.db.DatabaseService
import com.podcastlist.service.NotificationService
import com.podcastlist.ui.screen.PodcastListViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountService: AccountService,
    private val authorizationService: AuthorizationService,
    private val databaseService: DatabaseService
) : PodcastListViewModel() {
    var spotifyAppRemote = mutableStateOf<SpotifyAppRemote?>(null)
    var playerState = mutableStateOf<PlayerState?>(null)
    var isInternetAvailable = mutableStateOf(true)
    var isPowerSaveModeOn = mutableStateOf(false)
    var useSystemLightTheme = mutableStateOf(true)
    var darkTheme = mutableStateOf(false)
    var darkThemePowerSave = mutableStateOf(true)
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

    fun fetchUserSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                databaseService.getUserDocument()
                    .addOnSuccessListener { result ->
                        useSystemLightTheme.value =
                            if (result.data == null || result.data?.get("useSystemLightTheme") == null) true
                            else result.data?.get("useSystemLightTheme") as Boolean
                        darkTheme.value =
                            if (result.data == null || result.data?.get("darkTheme") == null) false
                            else result.data?.get("darkTheme") as Boolean
                        darkThemePowerSave.value =
                            if (result.data == null || result.data?.get("darkThemePowerSave") == null) true
                            else result.data?.get("darkThemePowerSave") as Boolean
                    }
                    .addOnFailureListener {
                        Log.d("DatabaseServiceImpl", "Failed to get mark status: $it")
                    }
            }
        }
    }

    fun setUseSystemLightThemeDB(value: Boolean) {
        viewModelScope.launch {
            catchException {
                databaseService.setUseSystemLightThemeSetting(value)
            }
        }
    }

    fun setDarkThemeDB(value: Boolean) {
        viewModelScope.launch {
            catchException {
                databaseService.setDarkThemeSetting(value)
            }
        }
    }

    fun setDarkThemePowerSaveDB(value: Boolean) {
        viewModelScope.launch {
            catchException {
                databaseService.setDarkThemePowerSaveSetting(value)
            }
        }
    }

}