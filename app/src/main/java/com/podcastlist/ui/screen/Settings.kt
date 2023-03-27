package com.podcastlist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    isAppInDarkTheme: Boolean,
    setColorTheme: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Dark theme",
            fontSize = 30.sp,
            style = MaterialTheme.typography.h4
        )

        Spacer(Modifier.weight(1f))
        Switch(
            checked = isAppInDarkTheme,
            onCheckedChange = { setColorTheme(it) },
            modifier = Modifier.scale(1.5f)
        )
    }
}