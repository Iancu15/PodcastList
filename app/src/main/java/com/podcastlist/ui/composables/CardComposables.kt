package com.podcastlist.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.podcastlist.api.model.Podcast
import com.podcastlist.api.model.Podcasts

@Composable
fun PodcastCard(
    podcast: Podcast,
    modifier: Modifier,
    cardsPerRow: Int
) {
    val cardPadding = 16.dp.div(cardsPerRow)
    val titleSize = 40.sp.div(cardsPerRow)
    val subtitleSize = 20.sp.div(cardsPerRow)
    val infoTransparency = 0.85f
    val infoHeight = 0.3f
    val cornerRoundness = 25.dp
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
                    .clip(RoundedCornerShape(cornerRoundness, cornerRoundness, 0.dp, 0.dp))
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
            Surface(
                modifier = Modifier
                    .fillMaxHeight(infoHeight)
                    .fillMaxWidth()
                    .alpha(infoTransparency)
                    .align(Alignment.BottomCenter),
                color = Color.Black
            ) {
                Column(
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                ) {
                    Text(
                        text = podcast.name,
                        fontSize = titleSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                    Text(
                        text = podcast.publisher,
                        fontSize = subtitleSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PodcastCardList(
    layoutPadding: Dp,
    cardHeight: Dp,
    cardsPerRow: Int,
    podcasts: List<Podcast>
) {
    LazyColumn(
        modifier = Modifier
            .padding(layoutPadding)
            .fillMaxSize()
    ) {
        items(
            podcasts.windowed(cardsPerRow, cardsPerRow, true)
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