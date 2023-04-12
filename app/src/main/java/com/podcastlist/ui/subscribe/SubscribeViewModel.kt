package com.podcastlist.ui.subscribe

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.Podcasts
import com.podcastlist.ui.AuthorizationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SubscribeViewModel @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val spotifyService: SpotifyService
) : AuthorizationViewModel(authorizationService) {
    var searchedPodcasts: List<Podcast> by mutableStateOf(arrayListOf())
    var searchQuery by mutableStateOf("")
        private set

    fun onSearchQueryChange(newValue: String) {
        searchQuery = newValue
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                searchedPodcasts = spotifyService.searchPodcasts(
                    authorization = authorizationService.authorizationToken,
                    searchQuery = searchQuery
                ).shows.items

                Log.d("HomeViewModel", "Fetched ${searchedPodcasts.size} searched podcasts")
            }
        }
    }
}