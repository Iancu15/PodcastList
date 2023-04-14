package com.podcastlist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.composables.BasicPopup
import com.podcastlist.ui.composables.PodcastCardList
import com.podcastlist.ui.core.ProgressLine
import com.podcastlist.ui.podcast.PodcastPopupContent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    cardsPerRow: Int,
    reload: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    viewModel.snackbarManager = snackbarManager

    LaunchedEffect(key1 = viewModel.subscribedPodcasts.items.isNotEmpty()) {
        viewModel.fetchSubscribedPodcastsWithSnackbar()
        progress = 1.0f
    }

    val cardHeight = 380.dp.div(cardsPerRow)
    val layoutPadding = 8.dp.div(cardsPerRow)
    var showPodcastPopup by remember { mutableStateOf(false) }
    var focusedPodcast by remember { mutableStateOf(Podcast("", "", "", arrayListOf())) }
    ProgressLine(progress = progress)
    BasicPopup(
        showPopup = showPodcastPopup,
        onDismiss = { showPodcastPopup = false },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colors.background)
    ) {
        PodcastPopupContent(podcast = focusedPodcast)
    }

    PodcastCardList(
        layoutPadding = layoutPadding,
        cardHeight = cardHeight,
        cardsPerRow = cardsPerRow,
        podcasts = viewModel.subscribedPodcasts.items.map { x -> x.show },
        onImageClick = {
            focusedPodcast = it
            showPodcastPopup = true
        }
    ) {
        viewModel.unsubscribeFromPodcast(it.id)
        reload()
    }
}