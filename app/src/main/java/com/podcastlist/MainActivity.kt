package com.podcastlist

import ScaffoldDeclaration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.HomeScreen
import com.podcastlist.ui.screen.login.LoginDrawerItem
import com.podcastlist.ui.screen.SettingsScreen
import com.podcastlist.ui.screen.edit_account.EditAccountScreen
import com.podcastlist.ui.screen.login.LoginScreen
import com.podcastlist.ui.screen.signup.SignUpScreen
import com.podcastlist.ui.screen.splash.SplashScreen
import com.podcastlist.ui.theme.MyApplicationTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Screen {
    HOME, SETTINGS, LOGIN, SPLASH, SIGNUP, EDIT_ACCOUNT
}
@Immutable
data class DrawerItemData(
    val buttonText: String,
    val iconImageVector: ImageVector,
    val iconDescriptionId: Int,
    val screen: Screen
)

val screenToTitleDict = mapOf(
    Screen.HOME to "Home",
    Screen.SETTINGS to "Settings",
    Screen.LOGIN to "Login",
    Screen.SIGNUP to "Register",
    Screen.EDIT_ACCOUNT to "Edit account information"
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val clientId = "fd24490669d84b619df5b235ba17e217"
    private val redirectUri = "podcastlist://podcastlist.com"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isSystemInDarkThemeValue = isSystemInDarkTheme()
            var isAppInDarkTheme by rememberSaveable { mutableStateOf(isSystemInDarkThemeValue) }
            MyApplicationTheme(darkTheme = isAppInDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScaffoldDeclaration(isAppInDarkTheme) {
                        newValue -> isAppInDarkTheme = newValue
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

}
