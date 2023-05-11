package com.podcastlist

import ScaffoldDeclaration
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.podcastlist.bcastreceiver.InternetStatusReceiver
import com.podcastlist.ui.theme.MyApplicationTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val clientId = "fd24490669d84b619df5b235ba17e217"
    private val redirectUri = "com.podcastlist://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCodeValue = 1337
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAuthorization()
        setContent {
            val isSystemInDarkThemeValue = isSystemInDarkTheme()
            var isAppInDarkTheme by rememberSaveable { mutableStateOf(isSystemInDarkThemeValue) }
            MyApplicationTheme(darkTheme = isAppInDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScaffoldDeclaration(isAppInDarkTheme, viewModel) {
                        newValue -> isAppInDarkTheme = newValue
                    }
                }
            }
        }

        val internetStatusReceiver = InternetStatusReceiver(viewModel)
        this.registerReceiver(internetStatusReceiver, IntentFilter())
    }

    private fun requestAuthorization() {
        val builder = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )

        builder.setScopes(arrayOf(
            "user-library-read",
            "user-library-modify",
            "user-read-playback-position"
        ))

        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, requestCodeValue, request)

    }

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                viewModel.spotifyAppRemote.value = appRemote
                viewModel.spotifyAppRemote.value!!.playerApi.subscribeToPlayerState().setEventCallback {
                    viewModel.playerState.value = it
                }
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

//    private fun connected() {
//        spotifyAppRemote?.let {
//            // Play a playlist
//            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
//            it.playerApi.play(playlistURI)
//            // Subscribe to PlayerState
//            it.playerApi.subscribeToPlayerState().setEventCallback {
//                val track: Track = it.track
//                Log.d("MainActivity", track.name + " by " + track.artist.name)
//            }
//        }
//    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        // Check if result comes from the correct activity
        if (requestCode == requestCodeValue) {
            val response: AuthorizationResponse =
                AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("MainActivity", "Authorization response successful")
                    viewModel.storeAuthorizationToken(
                        accessToken = response.accessToken,
                        expiresIn = response.expiresIn
                    )
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.d("MainActivity", "Authorization response failed: ${response.error}")
                }
                else -> {}
            }
        }
    }

}
