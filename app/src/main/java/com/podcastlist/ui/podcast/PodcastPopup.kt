package com.podcastlist.ui.podcast


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.MainActivityViewModel
import com.podcastlist.api.model.PodcastEpisode
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.core.ProgressLine
import com.podcastlist.ui.subscribe.SubscribeTabs
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun PodcastPopupContent(
    podcast: Podcast,
    mainActivityViewModel: MainActivityViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = podcast.name,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
            fontSize = 30.sp,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Divider()
        ShowEpisodes(podcast = podcast, mainActivityViewModel = mainActivityViewModel)
    }
}

@Composable
fun ShowEpisodes(
    viewModel: PodcastViewModel = hiltViewModel(),
    podcast: Podcast,
    mainActivityViewModel: MainActivityViewModel
) {
    var expandedEpisodeId by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = viewModel.episodes.items.isNotEmpty(), key2 = viewModel.shouldRefetch) {
        viewModel.shouldRefetch = false
        viewModel.fetchEpisodesOfPodcastWithSnackbar(podcast)
        progress = 1.0f
    }

    ProgressLine(progress = progress)
    viewModel.lazyListState = rememberLazyListState()

    var tabState by remember { mutableStateOf(0) }
    Column {
        PodcastListTabs(
            state = tabState,
            modifyState = { newValue -> tabState = newValue }
        )

        LazyColumn(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            state = viewModel.lazyListState
        ) {
            items(viewModel.episodes.items) {
                if ((tabState == 0 && !it.resume_point.fully_played) || (tabState == 1 && it.resume_point.fully_played)) {
                    ShowEpisode(
                        episode = it,
                        expandedEpisodeId = expandedEpisodeId,
                        mainActivityViewModel = mainActivityViewModel,
                        resetProgress = { progress = 0f }
                    ) {
                        expandedEpisodeId = it
                    }
                }
            }
        }
    }
}

@Composable
fun PodcastListTabs(
    state: Int,
    modifyState: (Int) -> Unit
) {
    val tabNames = listOf("Backlog", "Watched episodes")
    TabRow(selectedTabIndex = state) {
        tabNames.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = state == index,
                onClick = { modifyState(index) }
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
    resetProgress: () -> Unit,
    changeExpandedEpisodeId: (String) -> Unit
) {
    val duration = episode.duration_ms.toDuration(DurationUnit.MILLISECONDS)
    val durationString = duration.toComponents { minutes, seconds, _ ->
        String.format("%02d:%02d", minutes, seconds)
    }

    val wasPlayed = episode.resume_point.fully_played
    val currentTrack by remember { mutableStateOf(mainActivityViewModel.playerState.value!!.track) }
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
                        .fillMaxWidth(0.9f),
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(100),
                    color = playSurfaceColor,
                    modifier = Modifier.clickable {
                        if (mainActivityViewModel.spotifyAppRemote.value == null) {
                            Log.d("ShowEpisode", "SpotifyAppRemote is null")
                        }

                        Log.d("ShowEpisode","Trying to play ${episode.name} with uri: ${episode.uri}")
                        if (!isCurrentlyPlaying) {
                            mainActivityViewModel.spotifyAppRemote.value?.playerApi?.play(episode.uri)
                        } else if (!wasPlayed) {
                            mainActivityViewModel.spotifyAppRemote.value?.playerApi?.seekTo(episode.duration_ms)
                            viewModel.shouldRefetch = true
                            resetProgress()
                        }
                    }
                ) {
                    Icon(playIcon, null)
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
        } else {
            Divider()
        }
    }
}