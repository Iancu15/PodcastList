package com.podcastlist.db

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.podcastlist.auth.AccountService
import com.podcastlist.ui.snackbar.SnackbarManager
import javax.inject.Inject

class DatabaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val accountService: AccountService
) : DatabaseService {

    override suspend fun storeTimestampNote(
        timestamp: String,
        note: String,
        trackUri: String,
        snackbarManager: SnackbarManager
    ) {
        val data = hashMapOf(
            "timestamp" to timestamp,
            "note" to note
        )

        db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .collection("timestamps")
            .document(timestamp)
            .set(data)
            .addOnSuccessListener {
                Log.d("DatabaseServiceImpl", "Successfully stored timestamp note")
            }
            .addOnFailureListener {
                Log.d("DatabaseServiceImpl", "Failed to store timestamp note: $it")
                snackbarManager.showMessage("Failed to store timestamp note")
            }
    }

    override suspend fun getTimestampNotes(
        timestamp: String,
        trackUri: String,
    ): List<DocumentSnapshot> {
        var documents: List<DocumentSnapshot> = arrayListOf()
        db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .collection("timestamps")
            .get()
            .addOnSuccessListener { result ->
                Log.d("DatabaseServiceImpl", "Successfully got ${result.size()} timestamp notes")
                documents = result.documents
            }
            .addOnFailureListener {
                Log.d("DatabaseServiceImpl", "Failed to get timestamp notes: $it")
            }

        return documents
    }

}