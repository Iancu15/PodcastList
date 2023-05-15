package com.podcastlist.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.podcastlist.api.model.Podcast
import com.podcastlist.ui.screen.home.HomeViewModel
import kotlinx.coroutines.Job

data class TopIconProperties(
    val isVisibile: Boolean = true,
    val imageVector: ImageVector,
    val onClick: (Podcast) -> Unit
)
@Composable
fun PodcastCard(
    viewModel: HomeViewModel = hiltViewModel(),
    podcast: Podcast,
    modifier: Modifier,
    cardsPerRow: Int,
    subtitleContent: @Composable() (ColumnScope.(Podcast, TextUnit) -> Unit),
    //fetchSubtitleText: suspend ((String) -> Unit, Podcast) -> Unit,
    topRightIconProperties: TopIconProperties,
    topLeftIconProperties: TopIconProperties,
    onImageClick: (Podcast) -> Unit
) {
    val cardPadding = 16.dp.div(cardsPerRow)
    val titleSize = 40.sp.div(cardsPerRow)
    val subtitleSize = 25.sp.div(cardsPerRow)
    val infoTransparency = 0.85f
    val infoHeight = 0.3f
    val cornerRoundness = 25.dp
//    var subtitleText by remember { mutableStateOf("") }
//    LaunchedEffect(key1 = true) {
//        fetchSubtitleText( { newValue -> subtitleText = newValue }, podcast )
//    }

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
                    .clickable {
                        onImageClick(podcast)
                    }
            )
            Row {
                if (topLeftIconProperties.isVisibile) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .clickable {
                                topLeftIconProperties.onClick(podcast)
                            }
                    ) {
                        Icon(
                            topLeftIconProperties.imageVector,
                            contentDescription = null
                        )
                    }
                }
                Spacer(
                    Modifier.weight(1f)
                )
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .clickable {
                            topRightIconProperties.onClick(podcast)
                        }
                ) {
                    Icon(
                        topRightIconProperties.imageVector,
                        contentDescription = null
                    )
                }
            }
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
                    subtitleContent(this, podcast, subtitleSize)
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
//    fetchSubtitleText: suspend ((String) -> Unit, Podcast) -> Unit = { callback: (String) -> Unit, podcast: Podcast ->
//        callback(podcast.publisher)
//    },
    subtitleContent: @Composable() (ColumnScope.(Podcast, TextUnit) -> Unit) = { podcast: Podcast, subtitleSize: TextUnit ->
        Text(
            text = podcast.publisher,
            fontSize = subtitleSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )
    },
    podcasts: List<Podcast>,
    onImageClick: (Podcast) -> Unit = {},
    topRightIconProperties: TopIconProperties = TopIconProperties(imageVector = Icons.Default.Delete) {},
    topLeftIconProperties: TopIconProperties = TopIconProperties(false, Icons.Default.Delete) {}
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
                        cardsPerRow = cardsPerRow,
                        topRightIconProperties = topRightIconProperties,
                        topLeftIconProperties = topLeftIconProperties,
                        onImageClick = onImageClick,
                        subtitleContent = subtitleContent
                    )
                }
            }
        }
    }
}