package com.podcastlist.ui.screen

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.podcastlist.ui.composables.PodcastCardList
import com.podcastlist.ui.core.ProgressLine

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    cardsPerRow: Int
) {
    var progress by remember { mutableStateOf(0f) }

    viewModel.snackbarManager = snackbarManager
    LaunchedEffect(key1 = viewModel.subscribedPodcasts.items.isNotEmpty()) {
        viewModel.fetchSubscribedPodcastsWithSnackbar()
        progress = 1.0f
    }

    val cardHeight = 380.dp.div(cardsPerRow)
    val layoutPadding = 8.dp.div(cardsPerRow)
    ProgressLine(progress = progress)
    PodcastCardList(
        layoutPadding,
        cardHeight,
        cardsPerRow,
        viewModel.subscribedPodcasts.items.map { x -> x.show }
    )
}