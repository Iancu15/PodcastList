import android.content.res.Resources
import android.util.Log
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.podcastlist.*
import com.podcastlist.R
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.core.Drawer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScaffoldDeclaration(
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val SnackbarManager = SnackbarManager(scaffoldState, scope, keyboardController)
    var showTopBar by rememberSaveable { mutableStateOf(false) }
    val refreshButtonRotationAngle = remember { Animatable(0f) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (showTopBar)
                TopAppBar(
                    title = {
                        Text(screenToTitleDict.getOrDefault(currentScreen, stringResource(R.string.app_title)))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch(Dispatchers.Main) {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        keyboardController?.hide()
                                        drawerState.open()
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch(Dispatchers.Main) {
                                refreshButtonRotationAngle.animateTo(
                                    targetValue = 360f,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing
                                    )
                                )

                                refreshButtonRotationAngle.snapTo(0f)
                                screenToPathDict[currentScreen]?.let {
                                    navController.navigate(it)
                                }
                            }
                        }) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.rotate(refreshButtonRotationAngle.value)
                            )
                        }
                    }
                )
        }
    ) {
        Drawer(
            paddingValues = it,
            drawerState,
            navController,
            scope,
            SnackbarManager,
            currentScreen,
            isAppInDarkTheme,
            setColorTheme,
            { newValue -> showTopBar = newValue }
        ) { newScreen ->
            currentScreen = newScreen
        }
    }
}