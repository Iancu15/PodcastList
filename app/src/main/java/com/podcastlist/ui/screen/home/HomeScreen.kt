package com.podcastlist.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
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
    if (mainActivityViewModel.isInternetAvailable.value) {
        var progress by remember { mutableStateOf(0f) }
        viewModel.snackbarManager = snackbarManager

        LaunchedEffect(key1 = viewModel.subscribedPodcasts.items.isNotEmpty()) {
            viewModel.fetchSubscribedPodcastsWithSnackbar()
            progress = 1.0f
        }

        val cardHeight = 380.dp.div(cardsPerRow)
        val layoutPadding = 8.dp.div(cardsPerRow)
        var showPodcastPopup by remember { mutableStateOf(false) }
        var focusedPodcast by remember { mutableStateOf(Podcast("", "", "", arrayListOf(), "", 0)) }
        ProgressLine(progress = progress)
        BasicPopup(
            showPopup = showPodcastPopup,
            onDismiss = { showPodcastPopup = false },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f)
                .background(MaterialTheme.colors.background)
        ) {
            PodcastPopupContent(
                snackbarManager = snackbarManager,
                podcast = focusedPodcast,
                mainActivityViewModel,
                scope
            )
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
            subtitleContent = { podcast: Podcast, subtitleSize: TextUnit ->
                val numberOfEpisodesWatched = viewModel.getNumberOfEpisodesWatched(podcast.id).collectAsState(initial = 0).value.toString()
                Text(
                    text = "Watched $numberOfEpisodesWatched/${podcast.total_episodes}",
                    fontSize = subtitleSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White
                )
            },
//            fetchSubtitleText = { callback: (String) -> Unit, podcast: Podcast ->
//                val numberOfEpisodesWatched = viewModel.getNumberOfEpisodesWatched(podcast.id).collectAsState(initial = 0).toString()
//                callback("Watched $numberOfEpisodesWatched/${podcast.total_episodes}")
//            },
            topRightIconProperties = TopIconProperties(true, Icons.Default.PlayArrow) {
                viewModel.markWatchedEpisode(it) { episode ->
                    scope.launch(Dispatchers.IO) {
                        mainActivityViewModel.spotifyAppRemote.value?.playerApi?.play(episode.uri)
                    }
                }
            },
            topLeftIconProperties = TopIconProperties(imageVector = Icons.Default.Delete) {
                viewModel.unsubscribeFromPodcast(it.id)
                reload()
            }
        )
    } else {
        Text("Internet isn't available folks!")
    }
}