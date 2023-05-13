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
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
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
    var isEpisodeMarked = mutableStateOf(false)
    var episodeThatHasLock = mutableStateOf("")
    var episodesPageNumber = mutableStateOf(0)
    var currPodcastId = mutableStateOf("")
    var lock = ReentrantLock()
    fun onNoteChange(newValue: String) {
        note.value = newValue
    }

    fun onEditNoteChange(newValue: String) {
        editNote.value = newValue
    }
    private fun fetchEpisodesOfPodcast(podcast: Podcast) {
        viewModelScope.launch(Dispatchers.IO) {
            catchException {
                val offset = episodesPageNumber.value * 50
                episodes = spotifyService.getEpisodesOfPodcast(
                    authorization = authorizationService.authorizationToken,
                    podcastId = podcast.id,
                    offset = offset
                )

                episodes.items = episodes.items.sortedBy { it.release_date }
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

    fun setMarkStatusOfEpisode(trackUri: String, marked: Boolean) {
        viewModelScope.launch {
            catchException {
                databaseService.setMarkStatusOfEpisode(trackUri, marked)
            }
        }
    }

    fun fetchEpisodeMarked(trackUri: String) {
        viewModelScope.launch {
            catchException {
                databaseService.getEpisodeDocument(trackUri)
                    .addOnSuccessListener { result ->
                        lock.lock()
                        episodeThatHasLock.value = trackUri
                        val marked = if (result.data == null) false else result.data?.get("marked") as Boolean
                        isEpisodeMarked.value = marked
                        lock.unlock()
                    }
                    .addOnFailureListener {
                        Log.d("DatabaseServiceImpl", "Failed to get mark status: $it")
                    }
            }
        }
    }

    fun fetchEpisodesPageNumber(podcastId: String) {
        viewModelScope.launch {
            catchException {
                databaseService.getPodcastDocument(podcastId)
                    .addOnSuccessListener { result ->
                        val page = if (result.data == null || result.data?.get("page") == null) 0
                            else result.data?.get("page") as Long
                        episodesPageNumber.value = page.toInt()
                    }
                    .addOnFailureListener {
                        Log.d("DatabaseServiceImpl", "Failed to get mark status: $it")
                    }
            }
        }
    }

    fun setEpisodePageNumber(podcastId: String, page: Int) {
        viewModelScope.launch {
            catchException {
                databaseService.setCurrentEpisodesPage(podcastId, page)
            }
        }
    }
}