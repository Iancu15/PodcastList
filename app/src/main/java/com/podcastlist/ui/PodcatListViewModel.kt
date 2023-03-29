package com.podcastlist.ui.screen

import androidx.lifecycle.ViewModel
import com.podcastlist.ui.SnackbarManager

open class PodcastListViewModel : ViewModel() {
    lateinit var snackbarManager: SnackbarManager
}