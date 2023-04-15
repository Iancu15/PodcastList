package com.podcastlist.ui.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R
import com.podcastlist.Screen
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.login.LoginDrawerItem
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Immutable
data class DrawerItemData(
    val buttonText: String,
    val iconImageVector: ImageVector,
    val iconDescriptionId: Int,
    val screen: Screen
)

@Composable
fun Drawer(
    paddingValues: PaddingValues,
    drawerState: DrawerState,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarManager: SnackbarManager,
    currentScreen: Screen,
    cardsPerRow: Float,
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit,
    setShowTopBar: (Boolean) -> Unit,
    viewModel: MainActivityViewModel,
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
            scope,
            navController,
            snackbarManager,
            isAppInDarkTheme,
            cardsPerRow,
            setColorTheme,
            setShowTopBar,
            modifyScreen,
            { newValue -> isUserLoggedOut = newValue },
            viewModel
        )
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