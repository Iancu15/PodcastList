package com.podcastlist.ui.core

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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