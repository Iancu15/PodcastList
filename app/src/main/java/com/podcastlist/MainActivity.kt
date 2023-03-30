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

}

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
    setShowTopBar: (Boolean) -> Unit,
    viewModel: MainActivityViewModel = hiltViewModel(),
    modifyScreen: (Screen) -> Unit
) {
    val loginPath = stringResource(R.string.login_path)
    val homePath = stringResource(R.string.home_path)
    val settingsPath = stringResource(R.string.settings_path)
    val editAccountPath = stringResource(id = R.string.edit_account_path)
    var isUserLoggedOut by remember { mutableStateOf(viewModel.isUserLoggedOut()) }
    ModalDrawer(
        modifier = Modifier.padding(paddingValues),
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 10.dp, end = 10.dp)
            ) {
                if (isUserLoggedOut) {
                    LoginDrawerItem(
                        drawerState = drawerState,
                        scope = scope,
                        currentScreen = currentScreen
                    ) {
                        navController.navigate(loginPath)
                    }
                } else {
                    EmailDropdownButton(
                        navigateToEditAccount = { navController.navigate(editAccountPath) },
                        closeDrawer = { scope.launch(Dispatchers.Main) {
                            drawerState.close()
                        } }
                    ) {
                        newValue -> isUserLoggedOut = newValue
                    }
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
                }
            }
        }
    ) {
        NavHostDeclaration(
            navController,
            snackbarManager,
            isAppInDarkTheme,
            setColorTheme,
            setShowTopBar,
            modifyScreen,
            { newValue -> isUserLoggedOut = newValue }
        )
    }
}

@Composable
fun EmailDropdownButton(
    viewModel: MainActivityViewModel = hiltViewModel(),
    closeDrawer: () -> Unit,
    navigateToEditAccount: () -> Unit,
    setIsUserLoggedOut: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(15))
                .clickable {
                    expanded = true
                }
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                viewModel.getUserEmail()?.let {
                    Text(
                        text = it,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    if (expanded) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_left_48px),
                            contentDescription = stringResource(R.string.icon_dropdown_open)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_down_48px),
                            contentDescription = stringResource(R.string.dropdown_icon)
                        )
                    }

                    UserDropdownMenu(
                        setIsUserLoggedOut,
                        expanded,
                        navigateToEditAccount,
                        closeDrawer
                    ) {
                        expanded = false
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider()
    }
}

@Composable
fun UserDropdownMenu(
    setIsUserLoggedOut: (Boolean) -> Unit,
    expanded: Boolean,
    navigateToEditAccount: () -> Unit,
    closeDrawer: () -> Unit,
    viewModel: MainActivityViewModel = hiltViewModel(),
    dismissMenu: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = { dismissMenu() }) {
        DropdownMenuItem(onClick = {
            dismissMenu()
            closeDrawer()
            navigateToEditAccount()
        }) {
            Text(stringResource(R.string.edit_account_text))
        }

        Divider()
        DropdownMenuItem(onClick = {
            dismissMenu()
            viewModel.signOutUser()
            setIsUserLoggedOut(true)
        }) {
            Text(stringResource(R.string.sign_out_text))
        }
    }
}

@Composable
fun NavHostDeclaration(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit,
    setShowTopBar: (Boolean) -> Unit,
    modifyScreen: (Screen) -> Unit,
    setIsUserLoggedOut: (Boolean) -> Unit,
    viewModel: MainActivityViewModel = hiltViewModel()
) {
    val loginPath = stringResource(R.string.login_path)
    val homePath = stringResource(R.string.home_path)
    val settingsPath = stringResource(R.string.settings_path)
    val splashPath = stringResource(R.string.splash_path)
    val signUpPath = stringResource(R.string.signup_path)
    val editAccountPath = stringResource(R.string.edit_account_path)
    NavHost(
        navController = navController,
        startDestination = splashPath
    ) {
        composable(loginPath) {
            modifyScreen(Screen.LOGIN)
            LoginScreen(
                snackbarManager = snackbarManager,
                navigateHome = { navController.navigate(homePath) }
            ) {
                navController.navigate(signUpPath)
            }
        }

        composable(homePath) {
            modifyScreen(Screen.HOME)
            setIsUserLoggedOut(viewModel.isUserLoggedOut())
            HomeScreen()
        }

        composable(settingsPath) {
            modifyScreen(Screen.SETTINGS)
            SettingsScreen(isAppInDarkTheme, setColorTheme)
        }

        composable(splashPath) {
            modifyScreen(Screen.SPLASH)
            SplashScreen(setShowTopBar) {
                navController.navigate(homePath)
            }
        }

        composable(signUpPath) {
            modifyScreen(Screen.SIGNUP)
            SignUpScreen(snackbarManager = snackbarManager) {
                navController.navigate(loginPath)
            }
        }

        composable(editAccountPath) {
            modifyScreen(Screen.EDIT_ACCOUNT)
            EditAccountScreen(snackbarManager = snackbarManager) {
                navController.navigate(homePath)
            }
        }
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
                scope.launch(Dispatchers.Main) { drawerState.close() }
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