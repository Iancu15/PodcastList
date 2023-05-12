package com.podcastlist.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.api.model.Podcasts
import com.podcastlist.db.DatabaseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService @Inject constructor(
//    private val databaseService: DatabaseService,
//    private val spotifyService: SpotifyService,
//    private val authorizationService: AuthorizationService
) : Service() {
    private val TAG = "NotificationService"
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var podcastIds: List<String> = arrayListOf()

    override fun onCreate() {
//        scope.launch {
//            while (!authorizationService.isTokenAvailable) {
//                delay(1000)
//            }
//
//            podcastIds = spotifyService.getSubscribedPodcasts(
//                authorization = authorizationService.authorizationToken
//            ).items.map { it.show.id }
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            while (true) {
                delay(10000)
                Log.d(TAG, "Checking for updates...")
//                for (podcastId in podcastIds) {
//                    databaseService.getPodcastDocument(podcastId)
//                        .addOnSuccessListener { result ->
//                            val numberOfEpisodes = if (result.data == null) 0
//                                else result.data?.get("numberOfEpisodes") as Int
//                            Log.d(TAG, "$numberOfEpisodes")
//                        }
//                        .addOnFailureListener {
//                            Log.d(TAG, "Failed to get mark status: $it")
//                        }
//                }

                Log.d(TAG, "Done checking updates")
            }
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}