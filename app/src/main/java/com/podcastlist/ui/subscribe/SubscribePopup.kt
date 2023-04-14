package com.podcastlist.ui.subscribe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.R
import com.podcastlist.api.MAXIMUM_NUMBER_OF_IDS
import com.podcastlist.ui.composables.FormDivider
import com.podcastlist.ui.composables.PodcastCardList
import kotlinx.coroutines.delay

@Composable
fun SearchField(
    value: String,
    onSearchQueryChange: (String) -> Unit
) {
    TextField(
        singleLine = true,
        label = { Text(stringResource(R.string.search_label)) },
        onValueChange = { onSearchQueryChange(it) },
        value = value,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SubscribeTabs(
    state: Int,
    modifyState: (Int) -> Unit
) {
    val tabNames = listOf("Search podcast", "Selected podcasts")
    TabRow(selectedTabIndex = state) {
        tabNames.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = state == index,
                onClick = { modifyState(index) }
            )
        }
    }
}
@Composable
fun SearchResults(
    viewModel: SubscribeViewModel = hiltViewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(
            value = viewModel.searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange
        )

        if (viewModel.searchedPodcasts.isNotEmpty()) {
            PodcastCardList(
                layoutPadding = 8.dp,
                cardHeight = 100.dp,
                cardsPerRow = 3,
                podcasts = viewModel.searchedPodcasts,
                topRightIconImageVector = Icons.Default.Add
            ) { podcast ->
                viewModel.selectPodcast(podcast)
            }
        } else {
            Text(
                text = "No search results",
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun SelectedPodcasts(
    viewModel: SubscribeViewModel = hiltViewModel(),
    reloadHomePage: () -> Unit,
    modifyTabState: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.selectedPodcasts.isNotEmpty()) {
            if (viewModel.selectedPodcasts.size <= MAXIMUM_NUMBER_OF_IDS) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.subscribeToSelectedPodcasts()
                            reloadHomePage()
                        },
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text("Add selected podcasts")
                    }
                    Divider()
                    PodcastCardList(
                        layoutPadding = 8.dp,
                        cardHeight = 100.dp,
                        cardsPerRow = 3,
                        podcasts = viewModel.selectedPodcasts
                    ) { podcast ->
                        viewModel.unselectPodcast(podcast)
                        modifyTabState(0)
                        modifyTabState(1)
                    }
                }
            } else {
                viewModel.snackbarManager.showMessage("Can't subscribe to more than $MAXIMUM_NUMBER_OF_IDS podcasts at once")
            }
        } else {
            Text(
                text = "No selected podcasts",
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun SubscribePopupContent(reloadHomePage: () -> Unit) {
    var tabState by remember { mutableStateOf(0) }
    Column {
        SubscribeTabs(
            state = tabState,
            modifyState = { newValue -> tabState = newValue }
        )

        if (tabState == 0) {
            SearchResults()
        } else {
            SelectedPodcasts(
                reloadHomePage = reloadHomePage
            ) { newState ->
                tabState = newState
            }
        }
    }
}