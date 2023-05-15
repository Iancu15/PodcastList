package com.podcastlist.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.podcastlist.MainActivity
import com.podcastlist.R
import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.SpotifyService
import com.podcastlist.db.DatabaseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

data class ServicePodcast (
    val name: String,
    val id: String,
    val total_episodes: Int
)

private const val TAG = "NotificationService"
private const val CHANNEL_ID = "podcast_list"

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject
    lateinit var databaseService: DatabaseService

    @Inject
    lateinit var spotifyService: SpotifyService

    @Inject
    lateinit var authorizationService: AuthorizationService

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var podcasts: List<ServicePodcast> = arrayListOf()
    private var werePodcastsFetched = false
    private var lock = ReentrantLock()
    private var currentNotificationId = 0

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun fetchPodcasts() {
        scope.launch {
            while (!authorizationService.isTokenAvailable) {
                delay(1000)
            }

            podcasts = spotifyService.getSubscribedPodcasts(
                authorization = authorizationService.authorizationToken
            ).items.map { ServicePodcast(it.show.name, it.show.id, it.show.total_episodes) }
        }
    }

    private fun updatePodcastInDatabase(podcastId: String, numberOfEpisodes: Int) {
        scope.launch {
            databaseService.setTotalNumberOfEpisodes(podcastId, numberOfEpisodes)
        }
    }

    private fun getNotificationId(): Int {
        lock.lock()
        val notificationId = currentNotificationId
        currentNotificationId++
        lock.unlock()
        return notificationId
    }

    private fun sendNotification(textContent: String) {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.podcasts_48px)
            .setContentTitle("PodcastList")
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(getNotificationId(), builder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            if (!werePodcastsFetched) {
                fetchPodcasts()
                werePodcastsFetched = true
            }

            while (true) {
                delay(10000)
                Log.d(TAG, "Checking podcasts for updates...")
                for (podcast in podcasts) {
                    databaseService.getPodcastDocument(podcast.id)
                        .addOnSuccessListener { result ->
                            val numberOfEpisodesLong = if (result.data == null) 0
                                else result.data?.get("numberOfEpisodes") as Long
                            val numberOfEpisodes = numberOfEpisodesLong.toInt()
                            Log.d(TAG, "${podcast.name}: ${podcast.total_episodes} episodes available, $numberOfEpisodes episodes tracked in database")
                            if (numberOfEpisodes != 0) {
                                if (podcast.total_episodes > numberOfEpisodes) {
                                    val numberOfNewEpisodes =
                                        podcast.total_episodes - numberOfEpisodes
                                    val categoryLetter = if (numberOfNewEpisodes == 1) ""
                                        else "s"
                                    sendNotification("$numberOfNewEpisodes episode$categoryLetter released by ${podcast.name}")
                                } else if (podcast.total_episodes < numberOfEpisodes) {
                                    val numberOfRemovedEpisodes =
                                        numberOfEpisodes - podcast.total_episodes
                                    val categoryLetter = if (numberOfRemovedEpisodes == 1) ""
                                        else "s"
                                    sendNotification("$numberOfRemovedEpisodes episode$categoryLetter removed by ${podcast.name}")
                                }
                            }

                            if (podcast.total_episodes != numberOfEpisodes) {
                                updatePodcastInDatabase(podcast.id, podcast.total_episodes)
                            }
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Failed to get mark status: $it")
                        }
                }
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