package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
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
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screen {
    HOME, SETTINGS
}

@Immutable
data class DrawerItemData(
    val buttonText: String,
    val iconImageVector: ImageVector,
    val iconDescriptionId: Int,
    val screen: Screen
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScaffoldDeclaration()
                }
            }
        }
    }
}

@Composable
fun ScaffoldDeclaration() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("PodcastList")
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
        },
    ) {
        NavHostDeclaration(navController)
        Drawer(paddingValues = it, drawerState, navController, scope)
    }
}

@Composable
fun NavHostDeclaration(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen()
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}

@Composable
fun Drawer(
    paddingValues: PaddingValues,
    drawerState: DrawerState,
    navController: NavHostController,
    scope: CoroutineScope
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    ModalDrawer(
        modifier = Modifier.padding(paddingValues),
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 10.dp, end = 10.dp)
            ) {
                DrawerItem(
                    drawerState = drawerState,
                    scope = scope,
                    currentScreen = currentScreen,
                    drawerItemData = DrawerItemData(
                        buttonText = "Home",
                        iconImageVector = Icons.Default.Home,
                        iconDescriptionId = R.string.home_icon,
                        screen = Screen.HOME
                    )
                ) {
                    navController.navigate("home")
                    currentScreen = Screen.HOME
                }

                DrawerItem(
                    drawerState = drawerState,
                    scope = scope,
                    currentScreen = currentScreen,
                    drawerItemData = DrawerItemData(
                        buttonText = "Settings",
                        iconImageVector = Icons.Default.Settings,
                        iconDescriptionId = R.string.settings_icon,
                        screen = Screen.SETTINGS
                    )
                ) {
                    navController.navigate("settings")
                    currentScreen = Screen.SETTINGS
                }
            }
        }
    ) {

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
                style = MaterialTheme.typography.h6,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}