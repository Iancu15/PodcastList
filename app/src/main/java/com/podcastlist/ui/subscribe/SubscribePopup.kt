package com.podcastlist.ui.subscribe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.R
import com.podcastlist.ui.composables.PodcastCardList

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
                podcasts = viewModel.searchedPodcasts
            )
        } else {
            Text(
                text = "No search results",
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun SelectedPodcasts() {
    Text("selected")
}

@Composable
fun SubscribePopupContent() {
    var tabState by remember { mutableStateOf(0) }
    Column {
        SubscribeTabs(
            state = tabState,
            modifyState = { newValue -> tabState = newValue }
        )
        if (tabState == 0) {
            SearchResults()
        } else {
            SelectedPodcasts()
        }
    }
}