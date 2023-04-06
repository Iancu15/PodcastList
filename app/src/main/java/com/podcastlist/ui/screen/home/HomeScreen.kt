package com.podcastlist.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.podcastlist.api.model.SubscribedPodcast
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun PodcastCard(
    podcast: SubscribedPodcast,
    modifier: Modifier,
    cardsPerRow: Int
) {
    val cardPadding = 16.dp.div(cardsPerRow)
    val titleSize = 40.sp.div(cardsPerRow)
    val subtitleSize = 20.sp.div(cardsPerRow)
    val infoTransparency = 0.85f
    val infoHeight = 0.3f
    val cornerRoundness = 10
    Card(
        modifier = modifier.padding(cardPadding),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(model = podcast.images[0].url),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(cornerRoundness))
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
            Surface(
                modifier = Modifier.fillMaxHeight(infoHeight)
                    .fillMaxWidth()
                    .alpha(infoTransparency)
                    .align(Alignment.BottomCenter)
            ) {
                Column {
                    Text(
                        text = podcast.name,
                        fontSize = titleSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = podcast.publisher,
                        fontSize = subtitleSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager,
    cardsPerRow: Int
) {
    viewModel.snackbarManager = snackbarManager
    LaunchedEffect(key1 = viewModel.subscribedPodcasts.items.isNotEmpty()) {
        viewModel.fetchSubscribedPodcastsWithSnackbar()
    }

    val cardHeight = 380.dp.div(cardsPerRow)
    val layoutPadding = 8.dp.div(cardsPerRow)
    LazyColumn(
        modifier = Modifier
            .padding(layoutPadding)
            .fillMaxSize()
    ) {
        items(
            viewModel.subscribedPodcasts.items.map { x -> x.show }.windowed(cardsPerRow, cardsPerRow, true)
        ) {sublist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
            ) {
                sublist.forEach { item ->
                    PodcastCard(
                        podcast = item,
                        modifier = Modifier
                            .height(cardHeight)
                            .fillParentMaxWidth(1F / cardsPerRow),
                        cardsPerRow = cardsPerRow
                    )
                }
            }
        }
    }
}