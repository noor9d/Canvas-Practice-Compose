package com.imagination.canvaspractice.presentation.splash

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieConstants
import com.imagination.canvaspractice.R
import com.imagination.canvaspractice.presentation.navigation.BackStack
import com.imagination.canvaspractice.presentation.navigation.push
import com.imagination.canvaspractice.core.components.SynapsesLottieAnim
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    backStack: BackStack
) {

    LaunchedEffect(Unit) {
        viewModel.registerUserEvent(UserEvent.StartSplashTimer(3000L))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    println("SplashTag: Navigating to ${event.screen}")
                    backStack.clear()
                    backStack.push(event.screen)
                }
            }
        }
    }

    BackHandler(enabled = true) {
        // disable back press on splash
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasPracticeTheme.colorScheme.primary)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(95.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = CanvasPracticeTheme.typography.display,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
        ) {
            SynapsesLottieAnim(
                modifier = Modifier.height(26.dp),
                animationRes = R.raw.loading_white,
                iterations = LottieConstants.IterateForever
            ) {}
        }
    }
}