package com.podcastlist.ui.podcast

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.EpisodesQuery
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.Podcasts
import com.podcastlist.ui.AuthorizationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class PodcastViewModel @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val spotifyService: SpotifyService
) : AuthorizationViewModel(authorizationService) {
    var episodes: EpisodesQuery by mutableStateOf(EpisodesQuery())
    var shouldRefetch: Boolean by mutableStateOf(false)
    var lazyListState: LazyListState by mutableStateOf(LazyListState())
    private fun fetchEpisodesOfPodcast(podcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                episodes = spotifyService.getEpisodesOfPodcast(
                    authorization = authorizationService.authorizationToken,
                    podcastId = podcast.id,
                    offset = 0
                )

                Log.d("HomeViewModel", "Got ${episodes.items.size} episodes for ${podcast.name}")
            }
        }
    }

    fun fetchEpisodesOfPodcastWithSnackbar(podcast: Podcast) {
        callFunctionOrShowRetry("Your list of podcasts couldn't be retrieved") {
            fetchEpisodesOfPodcast(podcast = podcast)
        }
    }
}