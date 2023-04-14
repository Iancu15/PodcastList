package com.podcastlist.ui.snackbar

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.podcast.PodcastViewModel
import com.podcastlist.ui.screen.home.HomeViewModel
import com.podcastlist.ui.subscribe.SubscribeViewModel

@Composable
fun InitializeSnackbars(
    subscribeViewModel: SubscribeViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    podcastViewModel: PodcastViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager
) {
    subscribeViewModel.snackbarManager = snackbarManager
    homeViewModel.snackbarManager = snackbarManager
    podcastViewModel.snackbarManager = snackbarManager
}