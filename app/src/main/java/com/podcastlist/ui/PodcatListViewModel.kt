package com.podcastlist.ui.screen

import androidx.lifecycle.ViewModel
import com.podcastlist.ui.snackbar.SnackbarManager
import com.spotify.android.appremote.api.SpotifyAppRemote

open class PodcastListViewModel : ViewModel() {
    lateinit var snackbarManager: SnackbarManager
    suspend fun catchException(func: suspend () -> Unit) {
        try {
            func()
        } catch (e: Exception) {
            e.message?.let { snackbarManager.showMessage(it) }
        }
    }
}