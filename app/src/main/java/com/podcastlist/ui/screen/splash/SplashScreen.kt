package com.podcastlist.ui.screen.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.podcastlist.R
import kotlinx.coroutines.delay

private const val SPLASH_TIMEOUT = 1000L

@Composable
fun SplashScreen(
    navigateHome: () -> Unit,
    setShowTopBar: (Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    Column(
        modifier = Modifier.fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.podcasts_48px),
            contentDescription = stringResource(R.string.podcasts_decorative_icon),
            modifier = Modifier
                .size(300.dp)
                .scale(scale.value)
        )

        CircularProgressIndicator(color = MaterialTheme.colors.onBackground)
    }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )

        delay(SPLASH_TIMEOUT)
        viewModel.onAppStart()
        setShowTopBar(true)
        navigateHome()
    }
}