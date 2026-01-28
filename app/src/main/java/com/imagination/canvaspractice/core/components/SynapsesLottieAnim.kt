package com.imagination.canvaspractice.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SynapsesLottieAnim(
    modifier: Modifier = Modifier,
    animationRes: Int,
    iterations: Int = LottieConstants.IterateForever,
    onAnimationEnd: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = true
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationEnd()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}