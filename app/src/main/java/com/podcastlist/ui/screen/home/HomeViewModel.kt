package com.podcastlist.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.Podcasts
import com.podcastlist.ui.AuthorizationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyService: SpotifyService,
    private val authorizationService: AuthorizationService
) : AuthorizationViewModel(authorizationService) {
    var subscribedPodcasts: Podcasts by mutableStateOf(Podcasts())

    private fun fetchSubscribedPodcasts() {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                subscribedPodcasts = spotifyService.getSubscribedPodcasts(
                    authorization = authorizationService.authorizationToken
                )

                Log.d("HomeViewModel", "Got ${subscribedPodcasts.items.size} subscribed podcasts")
            }
        }
    }

    private fun removePodcastFromSubscribedList(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                spotifyService.unsubscribeFromPodcasts(
                    authorization = authorizationService.authorizationToken,
                    ids = id
                )

                snackbarManager.showMessage("Successfully unsubscribed from podcast!")
            }
        }
    }
    fun fetchSubscribedPodcastsWithSnackbar() {
        callFunctionOrShowRetry("Your list of podcasts couldn't be retrieved") {
            fetchSubscribedPodcasts()
        }
    }

    fun unsubscribeFromPodcast(id: String) {
        callFunctionOrShowRetry("Couldn't remove podcast from list") {
            removePodcastFromSubscribedList(id)
        }
    }
}