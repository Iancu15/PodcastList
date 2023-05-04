package com.podcastlist.db

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.podcastlist.ui.snackbar.SnackbarManager

interface DatabaseService {
    suspend fun storeOrEditTimestampNote(
        timestamp: String,
        note: String,
        trackUri: String,
        snackbarManager: SnackbarManager
    )

    suspend fun getTimestampNotes(trackUri: String): Task<QuerySnapshot>

    suspend fun deleteTimestampNote(trackUri: String, timestamp: String)

    suspend fun setMarkStatusOfEpisode(trackUri: String, marked: Boolean)

    suspend fun getEpisodeDocument(trackUri: String): Task<DocumentSnapshot>

    suspend fun setCurrentEpisodesPage(podcastId: String, page: Int)

    suspend fun getPodcastDocument(podcastId: String): Task<DocumentSnapshot>

}