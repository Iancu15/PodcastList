package com.podcastlist.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.PopupProperties
import com.podcastlist.ui.subscribe.SubscribePopupContent

@Composable
fun FloatingButtonPopup(
    showPopup: Boolean,
    onDismiss: () -> Unit
) {
    if (showPopup) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true),
            alignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
                    .background(MaterialTheme.colors.background)
            ) {
                SubscribePopupContent()
            }
        }
    }
}

@Composable
fun FloatingButton(
    onButtonPressed: () -> Unit
) {
    FloatingActionButton(onClick = onButtonPressed) {
        Icon(
            Icons.Default.Add, contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}