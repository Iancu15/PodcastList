package com.podcastlist.ui

import androidx.compose.material.ScaffoldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SnackbarManager constructor(
    private val scaffoldState: ScaffoldState,
    private val scope: CoroutineScope
) {
    fun showMessage(message: String) {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }
}