package com.podcastlist.ui

import android.util.Log
import com.podcastlist.api.AuthorizationService
import com.podcastlist.ui.screen.PodcastListViewModel
import javax.inject.Inject

open class AuthorizationViewModel @Inject constructor(
    private val authorizationService: AuthorizationService
) : PodcastListViewModel() {
    fun callFunctionOrShowRetry(message: String, func: () -> Unit) {
        Log.d("AuthorizationViewModel", "isTokenAvailable: ${authorizationService.isTokenAvailable}")
        if (authorizationService.isTokenAvailable) {
            func()
        } else {
            snackbarManager.showRetryMessage(message) {
                func()
            }
        }
    }
}