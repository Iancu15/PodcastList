package com.podcastlist.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.PodcastEpisode
import com.podcastlist.api.model.Podcasts
import com.podcastlist.ui.AuthorizationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    suspend fun getNumberOfEpisodesWatchedAsync(podcast: Podcast): String {
        return withContext(Dispatchers.IO) {
            val episodes = spotifyService.getEpisodesOfPodcast(
                authorization = authorizationService.authorizationToken,
                podcastId = podcast.id,
                offset = 0
            )

            val numberOfEpisodesWatched = try {
                episodes.items.map { it.resume_point.fully_played }.filter { it }.size
            } catch (_: Exception) {
                0
            }

            Log.d("HomeViewModel", "Watched $numberOfEpisodesWatched for ${podcast.name}")
            return@withContext "Watched $numberOfEpisodesWatched/${podcast.total_episodes}"
        }
    }

    fun markWatchedEpisode(podcast: Podcast, checkEpisode: (PodcastEpisode) -> Unit) {
        viewModelScope.launch {
            val episodes = spotifyService.getEpisodesOfPodcast(
                authorization = authorizationService.authorizationToken,
                podcastId = podcast.id,
                offset = 0
            )

            val watchedEpisodes = episodes.items.filter { it.resume_point.fully_played }
            var positionOfLastEpisodeWatched = -1
            if (watchedEpisodes.isNotEmpty()) {
                val lastEpisodeWatched = watchedEpisodes[watchedEpisodes.lastIndex]
                positionOfLastEpisodeWatched = episodes.items.indexOf(lastEpisodeWatched)
            }

            if (positionOfLastEpisodeWatched == episodes.items.size - 1) {
                snackbarManager.showMessage("Already watched all episodes")
            } else {
                val episodeToBeMarked = episodes.items[positionOfLastEpisodeWatched + 1]
                checkEpisode(episodeToBeMarked)
                Log.d("HomeViewModel", "Marked ${episodeToBeMarked.name} as watched")
            }
        }
    }
}