package com.podcastlist

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.HomeScreen
import com.podcastlist.ui.screen.LoginDrawerItem
import com.podcastlist.ui.screen.SettingsScreen
import com.podcastlist.ui.screen.LoginScreen
import com.podcastlist.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screen {
    HOME, SETTINGS, LOGIN
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
    Screen.SETTINGS to "Settings"
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isSystemInDarkThemeValue = isSystemInDarkTheme()
            var isAppInDarkTheme by remember { mutableStateOf(isSystemInDarkThemeValue) }
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

}

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
    val SnackbarManager = SnackbarManager(scaffoldState, scope)
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(screenToTitleDict.getOrDefault(currentScreen, stringResource(R.string.app_title)))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
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
            setColorTheme
        ) { newScreen ->
            currentScreen = newScreen
        }
    }
}

@Composable
fun NavHostDeclaration(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit
) {
    val loginPath = stringResource(R.string.login_path)
    val homePath = stringResource(R.string.home_path)
    val settingsPath = stringResource(R.string.settings_path)
    NavHost(
        navController = navController,
        startDestination = homePath
    ) {
        composable(loginPath) {
            LoginScreen(snackbarManager = snackbarManager)
        }

        composable(homePath) {
            HomeScreen()
        }

        composable(settingsPath) {
            SettingsScreen(isAppInDarkTheme, setColorTheme)
        }
    }
}

@Composable
fun Drawer(
    paddingValues: PaddingValues,
    drawerState: DrawerState,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarManager: SnackbarManager,
    currentScreen: Screen,
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit,
    modifyScreen: (Screen) -> Unit
) {
    val loginPath = stringResource(R.string.login_path)
    val homePath = stringResource(R.string.home_path)
    val settingsPath = stringResource(R.string.settings_path)
    ModalDrawer(
        modifier = Modifier.padding(paddingValues),
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 10.dp, end = 10.dp)
            ) {
                LoginDrawerItem(
                    drawerState = drawerState,
                    scope = scope,
                    currentScreen = currentScreen
                ) {
                    navController.navigate(loginPath)
                    modifyScreen(Screen.LOGIN)
                }

                DrawerItem(
                    drawerState = drawerState,
                    scope = scope,
                    currentScreen = currentScreen,
                    drawerItemData = DrawerItemData(
                        buttonText = stringResource(R.string.home_title),
                        iconImageVector = Icons.Default.Home,
                        iconDescriptionId = R.string.home_icon,
                        screen = Screen.HOME
                    )
                ) {
                    navController.navigate(homePath)
                    modifyScreen(Screen.HOME)
                }

                DrawerItem(
                    drawerState = drawerState,
                    scope = scope,
                    currentScreen = currentScreen,
                    drawerItemData = DrawerItemData(
                        buttonText = stringResource(R.string.settings_title),
                        iconImageVector = Icons.Default.Settings,
                        iconDescriptionId = R.string.settings_icon,
                        screen = Screen.SETTINGS
                    )
                ) {
                    navController.navigate(settingsPath)
                    modifyScreen(Screen.SETTINGS)
                }
            }
        }
    ) {
        NavHostDeclaration(navController, snackbarManager, isAppInDarkTheme, setColorTheme)
    }
}

@Composable
fun DrawerItem(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentScreen: Screen,
    drawerItemData: DrawerItemData,
    onNavigate: () -> Unit
) {
    val surfaceColor = if (currentScreen == drawerItemData.screen) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface

    Surface(
        color = surfaceColor,
        modifier = Modifier
            .padding(bottom = 5.dp)
            .clip(RoundedCornerShape(15))
            .clickable {
                onNavigate()
                scope.launch { drawerState.close() }
            }
            .fillMaxWidth()
            .height(50.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                drawerItemData.iconImageVector,
                contentDescription = stringResource(id = drawerItemData.iconDescriptionId),
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = drawerItemData.buttonText,
                style = MaterialTheme.typography.h4,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}