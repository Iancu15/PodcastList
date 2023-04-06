package com.podcastlist.ui.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R

@Composable
fun EmailDropdownButton(
    viewModel: MainActivityViewModel = hiltViewModel(),
    closeDrawer: () -> Unit,
    navigateToEditAccount: () -> Unit,
    setIsUserLoggedOut: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(15))
                .clickable {
                    expanded = true
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                viewModel.getUserEmail()?.let {
                    Text(
                        text = it,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    if (expanded) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_left_48px),
                            contentDescription = stringResource(R.string.icon_dropdown_open)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_down_48px),
                            contentDescription = stringResource(R.string.dropdown_icon)
                        )
                    }

                    UserDropdownMenu(
                        setIsUserLoggedOut,
                        expanded,
                        navigateToEditAccount,
                        closeDrawer
                    ) {
                        expanded = false
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider()
    }
}

@Composable
fun UserDropdownMenu(
    setIsUserLoggedOut: (Boolean) -> Unit,
    expanded: Boolean,
    navigateToEditAccount: () -> Unit,
    closeDrawer: () -> Unit,
    viewModel: MainActivityViewModel = hiltViewModel(),
    dismissMenu: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = { dismissMenu() }) {
        DropdownMenuItem(onClick = {
            dismissMenu()
            closeDrawer()
            navigateToEditAccount()
        }) {
            Text(stringResource(R.string.edit_account_text))
        }

        Divider()
        DropdownMenuItem(onClick = {
            dismissMenu()
            viewModel.signOutUser()
            setIsUserLoggedOut(true)
        }) {
            Text(stringResource(R.string.sign_out_text))
        }
    }
}
