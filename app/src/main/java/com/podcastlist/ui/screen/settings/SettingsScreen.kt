package com.podcastlist.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.podcastlist.MainActivityViewModel
import com.podcastlist.R

@Composable
fun SettingsScreen(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        SettingsSwitch(
            text = stringResource(R.string.switch_use_system_light_theme),
            checked = viewModel.useSystemLightTheme.value,
            onCheckedChange = {
                viewModel.useSystemLightTheme.value = it
                viewModel.setUseSystemLightThemeDB(it)
            }
        )
        SettingsSwitch(
            text = stringResource(R.string.dark_theme_setting_text),
            enabled = !viewModel.useSystemLightTheme.value,
            checked = viewModel.darkTheme.value,
            onCheckedChange = {
                viewModel.darkTheme.value = it
                viewModel.setDarkThemeDB(it)
            }
        )
        SettingsSwitch(
            text = stringResource(R.string.switch_power_save_dark_theme),
            checked = viewModel.darkThemePowerSave.value,
            onCheckedChange = {
                viewModel.darkThemePowerSave.value = it
                viewModel.setDarkThemePowerSaveDB(it)
            }
        )
    }
}