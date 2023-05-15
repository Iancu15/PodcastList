package com.podcastlist.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.PodcastEpisode
import com.podcastlist.api.model.Podcasts
import com.podcastlist.db.DatabaseService
import com.podcastlist.service.ServicePodcast
import com.podcastlist.ui.AuthorizationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.max
import javax.inject.Inject

const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyService: SpotifyService,
    private val authorizationService: AuthorizationService,
    private val databaseService: DatabaseService,
    private val dataStore: DataStore<Preferences>
) : AuthorizationViewModel(authorizationService) {
    var subscribedPodcasts: Podcasts by mutableStateOf(Podcasts())
    var arePodcastsStoredInStorage: Boolean by mutableStateOf(false)
    private fun fetchSubscribedPodcasts() {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                subscribedPodcasts = spotifyService.getSubscribedPodcasts(
                    authorization = authorizationService.authorizationToken
                )

                if (!arePodcastsStoredInStorage) {
                    storePodcasts(subscribedPodcasts)
                    arePodcastsStoredInStorage = true
                }

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

//    suspend fun getNumberOfEpisodesWatchedAsync(podcast: Podcast): String {
//        return withContext(Dispatchers.IO) {
//            Log.d("HomeViewModel", "Watched $numberOfEpisodesWatchedTotal for ${podcast.name}")
//            return@withContext "Watched $numberOfEpisodesWatchedTotal/${podcast.total_episodes}"
//        }
//    }

    fun markWatchedEpisode(podcast: Podcast, checkEpisode: (PodcastEpisode) -> Unit) {
        viewModelScope.launch {
            catchException {
                databaseService.getPodcastDocument(podcastId = podcast.id)
                    .addOnSuccessListener { result ->
                        val page = if (result.data == null) 0 else result.data?.get("page") as Long
                        viewModelScope.launch {
                            val episodes = spotifyService.getEpisodesOfPodcast(
                                authorization = authorizationService.authorizationToken,
                                podcastId = podcast.id,
                                offset = page.toInt() * 50
                            )

                            episodes.items = episodes.items.sortedBy { it.release_date }
                            val watchedEpisodes = episodes.items.filter { it.resume_point.fully_played }
                            var positionOfLastEpisodeWatched = -1
                            if (watchedEpisodes.isNotEmpty()) {
                                val lastEpisodeWatched = watchedEpisodes[watchedEpisodes.lastIndex]
                                positionOfLastEpisodeWatched = episodes.items.indexOf(lastEpisodeWatched)
                            }

                            if (positionOfLastEpisodeWatched == episodes.items.size - 1) {
                                snackbarManager.showMessage("Got to the end of the page")
                            } else {
                                val episodeToBeMarked = episodes.items[positionOfLastEpisodeWatched + 1]
                                checkEpisode(episodeToBeMarked)
                                Log.d("HomeViewModel", "Marked ${episodeToBeMarked.name} as watched")
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d("DatabaseServiceImpl", "Failed to get mark status: $it")
                    }
            }
        }
    }

    private suspend fun getNumberOfEpisodesWatchedAsync(podcast: Podcast): Int {
        val numberOfPages = (podcast.total_episodes / 50)
        var numberOfEpisodesWatchedTotal = 0
        for (page in 0..numberOfPages) {
            val episodes = spotifyService.getEpisodesOfPodcast(
                authorization = authorizationService.authorizationToken,
                podcastId = podcast.id,
                offset = page * 50
            )

            val numberOfEpisodesWatched = try {
                episodes.items.map { it.resume_point.fully_played }.filter { it }.size
            } catch (_: Exception) {
                0
            }

            numberOfEpisodesWatchedTotal += numberOfEpisodesWatched
        }

        return numberOfEpisodesWatchedTotal
    }

    private fun storePodcasts(podcasts: Podcasts) {
        for (item in podcasts.items) {
            val podcast = item.show
            viewModelScope.launch(Dispatchers.IO) {
                val numberOfEpisodesWatched = getNumberOfEpisodesWatchedAsync(podcast)
                val podcastKey = intPreferencesKey(podcast.id)
                dataStore.edit { podcasts ->
                    val currNumberOfEpisodesWatched = podcasts[podcastKey] ?: 0
                    if (currNumberOfEpisodesWatched < numberOfEpisodesWatched) {
                        Log.d(TAG, "${podcast.name}: Stored $numberOfEpisodesWatched episodes watched")
                        podcasts[podcastKey] = numberOfEpisodesWatched
                    }

                    Log.d(TAG, "${podcast.name}: DataStore is up to date")
                }
            }
        }
    }

    fun getNumberOfEpisodesWatched(podcastId: String): Flow<Int> {
        val podcastKey = intPreferencesKey(podcastId)
        return dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[podcastKey] ?: 0
            }
    }

}