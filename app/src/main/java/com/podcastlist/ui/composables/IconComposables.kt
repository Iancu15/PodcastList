package com.podcastlist.ui.composables

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.podcastlist.R

@Composable
fun PodcastIcon(modifier: Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.podcasts_48px),
        contentDescription = stringResource(R.string.podcasts_decorative_icon),
        modifier = modifier
    )
}