package com.podcastlist.ui.podcast


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R
import com.podcastlist.api.model.PodcastEpisode
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.core.ProgressLine
import com.podcastlist.ui.snackbar.SnackbarManager
import com.podcastlist.ui.subscribe.SubscribeTabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun PodcastPopupContent(
    snackbarManager: SnackbarManager,
    podcast: Podcast,
    mainActivityViewModel: MainActivityViewModel,
    scope: CoroutineScope
) {
    var isTitleExpanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = podcast.name,
            modifier = Modifier
                .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                .clickable {
                    isTitleExpanded = !isTitleExpanded
                },
            fontSize = 30.sp,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (isTitleExpanded) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = podcast.publisher,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier.padding(7.dp)
                ) {
                    Text(
                        podcast.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Divider()
        ShowEpisodes(
            snackbarManager,
            podcast = podcast,
            mainActivityViewModel = mainActivityViewModel,
            scope = scope
        )
    }
}

@Composable
fun ShowEpisodes(
    snackbarManager: SnackbarManager,
    viewModel: PodcastViewModel = hiltViewModel(),
    podcast: Podcast,
    scope: CoroutineScope,
    mainActivityViewModel: MainActivityViewModel
) {
    var expandedEpisodeId by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0f) }
    var tabState by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = viewModel.episodes.items.isNotEmpty(), key2 = viewModel.episodesPageNumber.value) {
        viewModel.snackbarManager = snackbarManager
        viewModel.currPodcastId.value = podcast.id
        viewModel.fetchEpisodesOfPodcastWithSnackbar(podcast)
        progress = 1.0f
    }

    LaunchedEffect(key1 = viewModel.currPodcastId.value) {
        viewModel.snackbarManager = snackbarManager
        viewModel.fetchEpisodesPageNumber(podcast.id)
    }

    ProgressLine(progress = progress)
    viewModel.lazyListState = rememberLazyListState()
    Column {
        PodcastListTabs(
            state = tabState,
            modifyState = { newValue -> tabState = newValue }
        )

        if (tabState == 2) {
            EditCuePoints(mainActivityViewModel = mainActivityViewModel)
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .weight(1f),
                state = viewModel.lazyListState
            ) {
                if (viewModel.episodes.items.isNotEmpty()) {
                    items(viewModel.episodes.items) {
                        var errorOccurred = false
                        var isShown = false
                        try {
                            isShown = (tabState == 0 && !it.resume_point.fully_played) || (tabState == 1 && it.resume_point.fully_played)
                        } catch (e: NullPointerException) {
                            errorOccurred = true
                        }

                        if (isShown) {
                            ShowEpisode(
                                episode = it,
                                expandedEpisodeId = expandedEpisodeId,
                                mainActivityViewModel = mainActivityViewModel,
                                scope = scope
                            ) { str ->
                                expandedEpisodeId = str
                            }
                        } else if (errorOccurred) {
                            Text(
                                text = "Couldn't load episode",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 20.sp
                            )
                            Divider()
                        }
                    }
                }
            }

            EpisodesPageTabs(
                numberOfEpisodes = podcast.total_episodes,
                state = viewModel.episodesPageNumber.value,
                modifyState = { newValue ->
                    viewModel.episodesPageNumber.value = newValue
                    viewModel.setEpisodePageNumber(podcast.id, viewModel.episodesPageNumber.value)
                }
            )
        }
    }
}

@Composable
fun EpisodesPageTabs(
    numberOfEpisodes: Int,
    state: Int,
    modifyState: (Int) -> Unit
) {
    val numberOfPages = (numberOfEpisodes / 50) + 1
    var stateValue = state
    if (state >= numberOfPages) {
        stateValue = 0
    }

    TabRow(selectedTabIndex = stateValue) {
        for (page in 1..numberOfPages) {
            val index = page - 1
            Tab(
                text = { Text(page.toString()) },
                selected = state == index,
                onClick = { modifyState(index) }
            )
        }
    }
}

@Composable
fun PodcastListTabs(
    state: Int,
    modifyState: (Int) -> Unit
) {
    val tabNames = listOf("Backlog", "Watched episodes", "Edit cue points")
    TabRow(selectedTabIndex = state) {
        tabNames.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = state == index,
                onClick = {
                    modifyState(index)
                }
            )
        }
    }
}

@Composable
fun ShowEpisode(
    viewModel: PodcastViewModel = hiltViewModel(),
    episode: PodcastEpisode,
    mainActivityViewModel: MainActivityViewModel,
    expandedEpisodeId: String,
    scope: CoroutineScope,
    changeExpandedEpisodeId: (String) -> Unit
) {
    val duration = episode.duration_ms.toDuration(DurationUnit.MILLISECONDS)
    val durationString = duration.toComponents { minutes, seconds, _ ->
        String.format("%02d:%02d", minutes, seconds)
    }

    val wasPlayed = episode.resume_point.fully_played
    val currentTrack = mainActivityViewModel.playerState.value!!.track
    val playbackPosition = mainActivityViewModel.playerState.value!!.playbackPosition
    val isCurrentlyPlaying = if (currentTrack == null) false else currentTrack.uri == episode.uri
    var playIcon = Icons.Default.PlayArrow
    if (wasPlayed) {
        playIcon = Icons.Default.CheckCircle
    } else if (isCurrentlyPlaying) {
        playIcon = Icons.Default.Check
    }

    val playSurfaceColor =
        if (wasPlayed) MaterialTheme.colors.secondary
        else MaterialTheme.colors.primary
    val resumePoint =
        if (isCurrentlyPlaying) playbackPosition
        else episode.resume_point.resume_position_ms

    val resumePointDuration = resumePoint.toDuration(DurationUnit.MILLISECONDS)
    val resumePointString = resumePointDuration.toComponents { minutes, seconds, _ ->
        String.format("%02d:%02d", minutes, seconds)
    }

    var isEpisodeMarked by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = viewModel.episodeThatHasLock.value, key2 = viewModel.isEpisodeMarked.value) {
        viewModel.fetchEpisodeMarked(episode.uri)
        if (viewModel.episodeThatHasLock.value == episode.uri) {
            isEpisodeMarked = viewModel.isEpisodeMarked.value
            viewModel.episodeThatHasLock.value = ""
        }
    }

    Column(
        modifier = Modifier.clickable {
            if (expandedEpisodeId == episode.id) {
                changeExpandedEpisodeId("")
            } else {
                changeExpandedEpisodeId(episode.id)
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = episode.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth(0.80f),
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(100),
                    color = playSurfaceColor,
                    modifier = Modifier
                        .clickable {
                            if (mainActivityViewModel.spotifyAppRemote.value == null) {
                                Log.d("ShowEpisode", "SpotifyAppRemote is null")
                            }

                            Log.d(
                                "ShowEpisode",
                                "Trying to play ${episode.name} with uri: ${episode.uri}"
                            )
                            if (!isCurrentlyPlaying) {
                                mainActivityViewModel.spotifyAppRemote.value?.playerApi?.play(
                                    episode.uri
                                )
                            } else if (!wasPlayed) {
                                mainActivityViewModel.spotifyAppRemote.value?.playerApi?.seekTo(
                                    episode.duration_ms * 2
                                )
                                mainActivityViewModel.spotifyAppRemote.value?.playerApi?.pause()
                                episode.resume_point.fully_played = true
                            }
                        }
                        .padding(end = 7.dp)
                ) {
                    Icon(playIcon, null)
                }
                if (isEpisodeMarked) {
                    Icon(
                        painterResource(id = R.drawable.stars_fill0_wght400_grad0_opsz48),
                        null,
                        modifier = Modifier
                            .clickable {
                                isEpisodeMarked = false
                                viewModel.setMarkStatusOfEpisode(episode.uri, false)
                            }
                            .size(30.dp),
                        tint = MaterialTheme.colors.primary
                    )
                } else {
                    Icon(
                        painterResource(id = R.drawable.star_fill0_wght400_grad0_opsz48),
                        null,
                        modifier = Modifier
                            .clickable {
                                isEpisodeMarked = true
                                viewModel.setMarkStatusOfEpisode(episode.uri, true)
                            }
                            .size(30.dp),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            Row {
                Text(
                    text = episode.release_date,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$resumePointString/",
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 11.sp
                )
                Text(
                    text = durationString,
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 11.sp
                )
            }
        }

        if (expandedEpisodeId == episode.id) {
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                Text(
                    episode.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isCurrentlyPlaying) {
            Divider(color = MaterialTheme.colors.primary)
        } else if (isEpisodeMarked) {
            Divider(color = MaterialTheme.colors.primaryVariant)
        } else {
            Divider()
        }
    }
}