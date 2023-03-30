import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.podcastlist.R
import com.podcastlist.Screen
import com.podcastlist.screenToTitleDict
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.core.Drawer
import kotlinx.coroutines.Dispatchers
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