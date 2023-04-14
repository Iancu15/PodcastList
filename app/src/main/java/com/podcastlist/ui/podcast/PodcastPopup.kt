package com.podcastlist.ui.podcast


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.api.model.Episode
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.core.ProgressLine
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun PodcastPopupContent(podcast: Podcast) {
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
        ShowEpisodes(podcast = podcast)
    }
}

@Composable
fun ShowEpisodes(
    viewModel: PodcastViewModel = hiltViewModel(),
    podcast: Podcast
) {
    var expandedEpisodeId by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = viewModel.episodes.items.isNotEmpty()) {
        viewModel.fetchEpisodesOfPodcastWithSnackbar(podcast)
        progress = 1.0f
    }

    ProgressLine(progress = progress)
    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        items(viewModel.episodes.items) {
            ShowEpisode(
                episode = it,
                expandedEpisodeId = expandedEpisodeId,
            ) {
                expandedEpisodeId = it
            }
            Divider()
        }
    }
}

@Composable
fun ShowEpisode(
    episode: Episode,
    expandedEpisodeId: String,
    changeExpandedEpisodeId: (String) -> Unit
) {
    val duration = episode.duration_ms.toDuration(DurationUnit.MILLISECONDS)
    val durationString =
        duration.toComponents { minutes, seconds, _ ->
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
                    color = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null
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
    }
}