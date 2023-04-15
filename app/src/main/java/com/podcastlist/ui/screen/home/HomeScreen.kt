package com.podcastlist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.podcastlist.MainActivityViewModel
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.composables.BasicPopup
import com.podcastlist.ui.composables.PodcastCardList
import com.podcastlist.ui.composables.TopIconProperties
import com.podcastlist.ui.core.ProgressLine
import com.podcastlist.ui.podcast.PodcastPopupContent
import kotlinx.coroutines.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    scope: CoroutineScope,
    mainActivityViewModel: MainActivityViewModel,
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
    var focusedPodcast by remember { mutableStateOf(Podcast("", "", "", arrayListOf(), "")) }
    ProgressLine(progress = progress)
    BasicPopup(
        showPopup = showPodcastPopup,
        onDismiss = { showPodcastPopup = false },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .fillMaxHeight(0.85f)
            .background(MaterialTheme.colors.background)
    ) {
        PodcastPopupContent(podcast = focusedPodcast, mainActivityViewModel)
    }

    PodcastCardList(
        layoutPadding = layoutPadding,
        cardHeight = cardHeight,
        cardsPerRow = cardsPerRow,
        podcasts = viewModel.subscribedPodcasts.items.map { x -> x.show },
        onImageClick = {
            focusedPodcast = it
            showPodcastPopup = true
        },
        fetchSubtitleText = { callback: (String) -> Unit, podcast: Podcast ->
            scope.launch {
                callback(viewModel.getNumberOfEpisodesWatchedAsync(podcast))
            }
        },
        topRightIconProperties = TopIconProperties(true, Icons.Default.Add) {
            viewModel.markWatchedEpisode(it) { episode ->
                scope.launch(Dispatchers.IO) {
                    mainActivityViewModel.spotifyAppRemote.value?.playerApi?.play(episode.uri)
                    delay(10)
                    mainActivityViewModel.spotifyAppRemote.value?.playerApi?.seekTo(episode.duration_ms * 2)
                }
            }
        },
        topLeftIconProperties = TopIconProperties(imageVector = Icons.Default.Delete) {
            viewModel.unsubscribeFromPodcast(it.id)
            reload()
        }
    )
}