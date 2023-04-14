import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.podcastlist.*
import com.podcastlist.R
import com.podcastlist.ui.composables.BasicPopup
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.core.Drawer
import com.podcastlist.ui.core.FloatingButton
import com.podcastlist.ui.menu.GridViewDropdownMenu
import com.podcastlist.ui.snackbar.InitializeSnackbars
import com.podcastlist.ui.subscribe.SubscribePopupContent
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScaffoldDeclaration(
    isAppInDarkTheme: Boolean,
    mainActivityViewModel: MainActivityViewModel,
    setColorTheme: (Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val scaffoldState = rememberScaffoldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarManager = SnackbarManager(scaffoldState, scope, keyboardController)
    var showTopBar by rememberSaveable { mutableStateOf(false) }
    val refreshButtonRotationAngle = remember { Animatable(0f) }
    var isGridViewDropdownMenuExpanded by remember { mutableStateOf(false) }
    var cardsPerRow by remember { mutableStateOf(2f) }
    var showAddPopup by remember { mutableStateOf(false) }
    val homePath = stringResource(R.string.home_path)

    InitializeSnackbars(snackbarManager = snackbarManager)
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
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.HOME) {
                            Column {
                                IconButton(onClick = {
                                    isGridViewDropdownMenuExpanded = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.grid_view_fill0_wght300_grad0_opsz48),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }

                                GridViewDropdownMenu(
                                    expanded = isGridViewDropdownMenuExpanded,
                                    cardsPerRow = cardsPerRow,
                                    { newValue -> cardsPerRow = newValue }
                                ) {
                                    isGridViewDropdownMenuExpanded = false
                                }
                            }
                        }

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
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.rotate(refreshButtonRotationAngle.value)
                            )
                        }
                    }
                )
        },
        floatingActionButton = {
            if (currentScreen == Screen.HOME && !showAddPopup) {
                FloatingButton { showAddPopup = true }
            }
        }
    ) {
        Column {
            Drawer(
                paddingValues = it,
                drawerState,
                navController,
                scope,
                snackbarManager,
                currentScreen,
                cardsPerRow,
                isAppInDarkTheme,
                setColorTheme,
                { newValue -> showTopBar = newValue },
                mainActivityViewModel
            ) { newScreen ->
                currentScreen = newScreen
            }

            BasicPopup(
                showPopup = showAddPopup,
                onDismiss = { showAddPopup = false },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .background(MaterialTheme.colors.background)
            ) {
                SubscribePopupContent {
                    navController.navigate(homePath)
                    showAddPopup = false
                }
            }
        }
    }
}