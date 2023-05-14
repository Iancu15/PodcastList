package com.podcastlist

import ScaffoldDeclaration
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
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
import com.podcastlist.bcastreceiver.PowerSaveModeReceiver
import com.podcastlist.service.NotificationService
import com.podcastlist.ui.theme.MyApplicationTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val clientId = "fd24490669d84b619df5b235ba17e217"
    private val redirectUri = "com.podcastlist://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val requestCodeValue = 1337
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var powerSaveModeReceiver: PowerSaveModeReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAuthorization()
        setContent {
            var isAppInDarkTheme = isSystemInDarkTheme()
            if (viewModel.isPowerSaveModeOn.value) {
                isAppInDarkTheme = viewModel.darkThemePowerSave.value
            } else if (!viewModel.useSystemLightTheme.value) {
                isAppInDarkTheme = viewModel.darkTheme.value
            }

            MyApplicationTheme(darkTheme = isAppInDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScaffoldDeclaration(viewModel)
                }
            }
        }
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
        viewModel.fetchUserSettings()
        val pm: PowerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm.isPowerSaveMode) {
            Log.d(TAG, "Power save mode is on")
            viewModel.isPowerSaveModeOn.value = true
        } else {
            Log.d(TAG, "Power save mode is off")
            viewModel.isPowerSaveModeOn.value = false
        }

        if (!viewModel.isPowerSaveModeOn.value) {
            Intent(this, NotificationService::class.java).also {
                startService(it)
            }
        }

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

        powerSaveModeReceiver = PowerSaveModeReceiver(viewModel)
        val filter = IntentFilter()
        filter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED")
        registerReceiver(powerSaveModeReceiver, filter)
    }
    override fun onStop() {
        super.onStop()
        this.unregisterReceiver(powerSaveModeReceiver)
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
