package com.podcastlist.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.podcast.PodcastPopupContent

@Composable
fun BasicPopup(
    showPopup: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier,
    content: @Composable() (BoxScope.() -> Unit)
) {
    if (showPopup) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true),
            alignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
            ) {
                content()
            }
        }
    }
}