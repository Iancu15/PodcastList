package com.podcastlist.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.podcastlist.R

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
            text = stringResource(R.string.dark_theme_setting_text),
            fontSize = 30.sp,
            style = MaterialTheme.typography.h4
        )

        Spacer(Modifier.weight(1f))
        Switch(
            checked = isAppInDarkTheme,
            onCheckedChange = { setColorTheme(it) },
            modifier = Modifier.scale(1.5f),
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray
            )
        )
    }
}