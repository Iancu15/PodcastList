package com.podcastlist.ui.podcast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R
import com.podcastlist.ui.core.ProgressLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun NoteField(
    value: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        onValueChange = { onNoteChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.note_field_placeholder)) },
        modifier = modifier.padding(10.dp)
    )
}

@Composable
fun DisplayTimestampNotes(
    viewModel: PodcastViewModel = hiltViewModel(),
    mainActivityViewModel: MainActivityViewModel,
    reloadTab: () -> Unit
) {
    var timestampNoteBeingEdited by remember { mutableStateOf(-1L) }
    val editNote by viewModel.editNote
    val trackUri = mainActivityViewModel.playerState.value!!.track.uri

    LazyColumn(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        items(viewModel.timestampNotes) {
            val timestampDuration = it.timestamp.toDuration(DurationUnit.MILLISECONDS)
            val timestampString = timestampDuration.toComponents { minutes, seconds, _ ->
                String.format("%02d:%02d", minutes, seconds)
            }

            Column(
                modifier = Modifier.padding(top = 5.dp)
            ) {
                Divider()
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Text(timestampString)
                    Spacer(modifier = Modifier.width(30.dp))
                    if (timestampNoteBeingEdited == it.timestamp) {
                        NoteField(
                            value = editNote,
                            onNoteChange = viewModel::onEditNoteChange,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    } else {
                        Text(
                            text = it.note,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            modifier = Modifier
                                .clickable {
                                    val timestamp =
                                        mainActivityViewModel.playerState.value!!.playbackPosition.toString()
                                    viewModel.deleteTimestampNote(trackUri, timestamp)
                                    reloadTab()
                                }
                                .size(25.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (timestampNoteBeingEdited == it.timestamp) {
                            Icon(
                                Icons.Default.Done,
                                null,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.storeOrEditTimestampNote(it.timestamp.toString(), editNote, trackUri)
                                        timestampNoteBeingEdited = -1
                                        reloadTab()
                                    }
                                    .size(25.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Edit,
                                null,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.editNote.value = it.note
                                        timestampNoteBeingEdited = it.timestamp
                                    }
                                    .size(25.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCuePoints(
    mainActivityViewModel: MainActivityViewModel,
    viewModel: PodcastViewModel = hiltViewModel(),
    reloadTab: () -> Unit
) {
    val currentTrack = mainActivityViewModel.playerState.value!!.track
    val isPaused = mainActivityViewModel.playerState.value!!.isPaused
    val note by viewModel.note
    var progress by remember { mutableStateOf(0f) }
    val trackUri = mainActivityViewModel.playerState.value!!.track.uri
    LaunchedEffect(key1 = viewModel.timestampNotes.isNotEmpty(), key2 = trackUri) {
        val track = mainActivityViewModel.playerState.value!!.track
        if (track != null) {
            viewModel.fetchTimestampNotes(track.uri)
            progress = 1.0f
        }
    }

    ProgressLine(progress = progress)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        if (currentTrack != null && currentTrack.isPodcast) {
            Text(
                text = currentTrack.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                fontSize = 20.sp
            )
            Divider(color = MaterialTheme.colors.primary)
            NoteField(value = note, onNoteChange = viewModel::onNoteChange)
            Row {
                if (isPaused) {
                    Button(
                        onClick = {
                            mainActivityViewModel.spotifyAppRemote.value?.playerApi?.resume()
                        }
                    ) {
                        Text("Resume episode")
                    }
                } else {
                    Button(
                        onClick = {
                            mainActivityViewModel.spotifyAppRemote.value?.playerApi?.pause()
                        }
                    ) {
                        Text("Pause episode")
                    }
                }
                Spacer(modifier = Modifier.width(40.dp))
                Button(
                    onClick = {
                        val timestamp = mainActivityViewModel.playerState.value!!.playbackPosition.toString()
                        viewModel.storeOrEditTimestampNote(timestamp, note, trackUri)
                        reloadTab()
                    }
                ) {
                    Text("Add note")
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            DisplayTimestampNotes(reloadTab = reloadTab, mainActivityViewModel = mainActivityViewModel)
        } else {
            Text(
                text = "Play the episode you want to track",
                modifier = Modifier.padding(start = 35.dp)
            )
        }
    }
}