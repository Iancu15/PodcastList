package com.podcastlist.ui.podcast

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R

@Composable
fun NoteField(
    value: String,
    onNoteChange: (String) -> Unit
) {
    TextField(
        onValueChange = { onNoteChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.note_field_placeholder)) },
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
fun EditCuePoints(
    mainActivityViewModel: MainActivityViewModel,
    viewModel: PodcastViewModel = hiltViewModel()
) {
    val currentTrack = mainActivityViewModel.playerState.value!!.track
    val isPaused = mainActivityViewModel.playerState.value!!.isPaused
    val note by viewModel.note

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        if (currentTrack.isPodcast) {
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
                        val trackUri = mainActivityViewModel.playerState.value!!.track.uri
                        viewModel.storeTimestampNote(timestamp, note, trackUri)
                    }
                ) {
                    Text("Add note")
                }
            }
        } else {
            Text(
                text = "Play the episode you want to track",
                modifier = Modifier.padding(start = 35.dp)
            )
        }
    }
}