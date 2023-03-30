package com.podcastlist.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
class SnackbarManager constructor(
    private val scaffoldState: ScaffoldState,
    private val scope: CoroutineScope,
    private val keyboardController: SoftwareKeyboardController?
) {
    fun showMessage(message: String, hideKeyboard: Boolean = false) {
        scope.launch(Dispatchers.Main) {
            if (hideKeyboard)
                keyboardController?.hide()
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }
}