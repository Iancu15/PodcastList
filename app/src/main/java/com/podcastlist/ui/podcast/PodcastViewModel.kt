package com.podcastlist.ui.podcast

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.EpisodesQuery
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.Podcasts
import com.podcastlist.db.DatabaseService
import com.podcastlist.db.model.TimestampNote
import com.podcastlist.ui.AuthorizationViewModel
import com.podcastlist.ui.screen.edit_account.EditAccountUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class PodcastViewModel @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val spotifyService: SpotifyService,
    private val databaseService: DatabaseService
) : AuthorizationViewModel(authorizationService) {
    var episodes: EpisodesQuery by mutableStateOf(EpisodesQuery())
    var lazyListState: LazyListState by mutableStateOf(LazyListState())
    var note = mutableStateOf(String())
    var timestampNotes: List<TimestampNote> by mutableStateOf(arrayListOf())
    var editNote = mutableStateOf(String())
    fun onNoteChange(newValue: String) {
        note.value = newValue
    }

    fun onEditNoteChange(newValue: String) {
        editNote.value = newValue
    }
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

    fun storeOrEditTimestampNote(timestamp: String, note: String, trackUri: String) {
        viewModelScope.launch {
            catchException {
                databaseService.storeOrEditTimestampNote(timestamp, note, trackUri, snackbarManager)
            }
        }
    }

    fun deleteTimestampNote(trackUri: String, timestamp: String) {
        viewModelScope.launch {
            catchException {
                databaseService.deleteTimestampNote(trackUri, timestamp)
            }
        }
    }

    fun fetchTimestampNotes(trackUri: String) {
        viewModelScope.launch {
            catchException {
                databaseService.getTimestampNotes(trackUri)
                    .addOnSuccessListener { result ->
                        Log.d("DatabaseServiceImpl", "Successfully got ${result.size()} timestamp notes")
                        timestampNotes = result!!.map { TimestampNote((it.data["timestamp"] as String).toLong(),
                            it.data["note"] as String
                        ) }.sortedBy { it.timestamp }
                    }
                    .addOnFailureListener {
                        Log.d("DatabaseServiceImpl", "Failed to get timestamp notes: $it")
                    }
            }
        }
    }
}