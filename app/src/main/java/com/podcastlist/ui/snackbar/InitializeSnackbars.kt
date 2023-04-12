package com.podcastlist.ui.snackbar

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.subscribe.SubscribeViewModel

@Composable
fun InitializeSnackbars(
    subscribeViewModel: SubscribeViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager
) {
    subscribeViewModel.snackbarManager = snackbarManager
}