package com.podcastlist.ui.snackbar

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun showRetryMessage(
        message: String,
        retryAction: () -> Unit
    ) {
        var snackbarStatus: SnackbarResult
        scope.launch(Dispatchers.Main) {
            snackbarStatus = scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Retry"
            )

            if (snackbarStatus == SnackbarResult.ActionPerformed) {
                withContext(Dispatchers.IO) {
                    retryAction()
                }
            }
        }
    }

    fun showKeyboard() {
        keyboardController?.show()
    }
}