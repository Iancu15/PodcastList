package com.podcastlist.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.podcastlist.auth.AccountService
import com.podcastlist.ui.snackbar.SnackbarManager
import javax.inject.Inject

class DatabaseServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val accountService: AccountService
) : DatabaseService {

    override suspend fun storeOrEditTimestampNote(
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
                //snackbarManager.showMessage("Successfully stored timestamp note")
            }
            .addOnFailureListener {
                Log.d("DatabaseServiceImpl", "Failed to store timestamp note: $it")
                //snackbarManager.showMessage("Failed to store timestamp note")
            }
    }

    override suspend fun getTimestampNotes(
        trackUri: String,
    ): Task<QuerySnapshot> {
        return db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .collection("timestamps")
            .get()
    }

    override suspend fun deleteTimestampNote(trackUri: String, timestamp: String) {
        db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .collection("timestamps")
            .document(timestamp)
            .delete()
            .addOnSuccessListener {
                Log.d("DatabaseServiceImpl", "Successfully deleted timestamp note $timestamp")
            }
            .addOnFailureListener {
                Log.d("DatabaseServiceImpl", "Failed to delete timestamp note $timestamp: $it")
            }
    }

    override suspend fun setMarkStatusOfEpisode(trackUri: String, marked: Boolean) {
        val data = hashMapOf(
            "marked" to marked
        )

        db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("DatabaseServiceImpl", "Updated mark status of $trackUri to $marked")
            }
            .addOnFailureListener {
                Log.d("DatabaseServiceImpl", "Failed to update mark status: $it")
            }
    }

    override suspend fun getMarkStatusOfEpisode(trackUri: String): Task<DocumentSnapshot> {
        return db.collection("users")
            .document(accountService.currentUserId)
            .collection("tracks")
            .document(trackUri)
            .get()
    }

}