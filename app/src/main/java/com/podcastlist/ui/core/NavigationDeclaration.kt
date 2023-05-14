package com.podcastlist.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R
import com.podcastlist.Screen
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.HomeScreen
import com.podcastlist.ui.screen.edit_account.EditAccountScreen
import com.podcastlist.ui.screen.login.LoginScreen
import com.podcastlist.ui.screen.settings.SettingsScreen
import com.podcastlist.ui.screen.signup.SignUpScreen
import com.podcastlist.ui.screen.splash.SplashScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavHostDeclaration(
    scope: CoroutineScope,
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    cardsPerRow: Float,
    setShowTopBar: (Boolean) -> Unit,
    modifyScreen: (Screen) -> Unit,
    setIsUserLoggedOut: (Boolean) -> Unit,
    viewModel: MainActivityViewModel
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
            HomeScreen(
                scope = scope,
                snackbarManager = snackbarManager,
                cardsPerRow = cardsPerRow.toInt(),
                mainActivityViewModel = viewModel
            ) {
                navController.navigate(homePath)
            }
        }

        composable(settingsPath) {
            modifyScreen(Screen.SETTINGS)
            SettingsScreen(viewModel)
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