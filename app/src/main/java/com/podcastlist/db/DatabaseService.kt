package com.podcastlist.db

import com.google.firebase.firestore.DocumentSnapshot
import com.podcastlist.ui.snackbar.SnackbarManager

interface DatabaseService {
    suspend fun storeTimestampNote(
        timestamp: String,
        note: String,
        trackUri: String,
        snackbarManager: SnackbarManager
    )

    suspend fun getTimestampNotes(
        timestamp: String,
        trackUri: String
    ): List<DocumentSnapshot>
}