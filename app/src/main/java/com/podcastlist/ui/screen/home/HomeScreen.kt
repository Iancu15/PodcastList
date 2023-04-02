package com.podcastlist.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.ui.SnackbarManager
import com.podcastlist.ui.screen.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackbarManager: SnackbarManager
) {
    viewModel.snackbarManager = snackbarManager
    viewModel.getSubscribedPodcasts()
//    LazyColumn {
//        items(
//            viewModel.subscribedPodcasts.items.map { x -> x.show }
//        ) {
//            Text(it.name)
//        }
//    }
}