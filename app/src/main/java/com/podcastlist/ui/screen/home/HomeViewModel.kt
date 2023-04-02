package com.podcastlist.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.SubscribedPodcasts
import com.podcastlist.ui.screen.PodcastListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyService: SpotifyService,
    private val authorizationService: AuthorizationService
) : PodcastListViewModel() {
    var subscribedPodcasts: SubscribedPodcasts by mutableStateOf(SubscribedPodcasts())
    fun getSubscribedPodcasts() {
        viewModelScope.launch(Dispatchers.IO) {
            authorizationService.refreshAccessToken()
            Log.d("HomeViewModel", "Refresh token is ${authorizationService.authorizationTokena}")
            catchException {
                subscribedPodcasts = spotifyService.getSubscribedPodcasts(
                    authorization = authorizationService.authorizationToken
                )
            }
        }
    }
}