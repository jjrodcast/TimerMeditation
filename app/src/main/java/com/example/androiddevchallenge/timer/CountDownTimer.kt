/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.timer

import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.LottieAnimationState
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.theme.circleColor
import com.example.androiddevchallenge.ui.theme.grayBackground
import com.example.androiddevchallenge.ui.theme.purpleButton
import com.example.androiddevchallenge.ui.theme.ringColor

@ExperimentalAnimationApi
@Composable
fun CountDownTimerScreen(timerViewModel: TimerViewModel) {
    val stateObserver = timerViewModel.modelState.observeAsState(initial = TimerModel())
    val timerModel = stateObserver.value
    val rememberLottieAnimation = rememberLottieAnimationState(repeatCount = Int.MAX_VALUE)
    if (timerModel.state == State.FINISHED) rememberLottieAnimation.toggleIsPlaying()
    Surface(
        Modifier
            .fillMaxSize(),
        color = grayBackground
    ) {
        Box(Modifier.fillMaxSize()) {
            LottieLoader(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                R.raw.clouds,
                rememberLottieAnimation
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                CountDownTimerHeader(
                    timerModel,
                    onPlay = { timerViewModel.createTimer() },
                    onPause = { timerViewModel.stopTimer() },
                    onReset = { timerViewModel.resetTime() }
                )
                LottieLoader(
                    Modifier
                        .requiredSize(280.dp)
                        .align(Alignment.CenterHorizontally),
                    R.raw.meditation,
                    rememberLottieAnimation
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownTimerHeader(
    timerModel: TimerModel,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(460.dp)
            .fillMaxWidth()
    ) {
        if (timerModel.state != State.FINISHED) {
            Text(
                text = stringResource(R.string.timer_title),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 24.sp,
                color = MaterialTheme.colors.primary
            )
        }
        Column(Modifier.weight(1f)) {
            AnimatedVisibility(
                visible = timerModel.state == State.FINISHED,
                initiallyVisible = false
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.meditation_finished),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
            AnimatedVisibility(
                visible = timerModel.state != State.FINISHED,
                initiallyVisible = true
            ) {
                CounterCircle(Modifier.padding(32.dp), timerModel)
            }
        }
        AnimatedVisibility(visible = timerModel.state != State.FINISHED, initiallyVisible = true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val icon = if (timerModel.state == State.IDLE) Icons.Default.PlayArrow
                else Icons.Default.Pause

                CountDownButton(
                    onClick = {
                        if (timerModel.state == State.IDLE) onPlay()
                        else if (timerModel.state == State.RUNNING) onPause()
                    },
                    modifier = Modifier.background(purpleButton),
                    icon = icon
                )
                if (timerModel.state == State.IDLE) {
                    CountDownButton(
                        onClick = onReset,
                        modifier = Modifier.background(purpleButton),
                        icon = Icons.Default.Replay
                    )
                }
            }
        }
    }
}

@Composable
fun CounterCircle(modifier: Modifier = Modifier, timerModel: TimerModel) {
    val angle by animateFloatAsState(timerModel.timeInMillis * 360 / DEFAULT_TIMER.toFloat())
    Box(modifier) {
        Canvas(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            val minDimension = this.size.minDimension
            val currentRadius = minDimension / 2f
            val circleRadius = currentRadius * 0.85f
            drawCircle(circleColor, radius = circleRadius)
            drawArc(
                color = ringColor,
                startAngle = 270f,
                sweepAngle = -angle,
                useCenter = false,
                topLeft = Offset(
                    x = this.center.x - currentRadius,
                    y = this.center.y - currentRadius
                ),
                size = Size(width = minDimension, height = minDimension),
                style = Stroke(width = 22.dp.value, cap = StrokeCap.Round)
            )
        }
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timerModel.timeWithFormat,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 48.sp
            )
        }
    }
}

@Composable
fun CountDownButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .requiredSize(50.dp)
        ) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
fun LottieLoader(
    modifier: Modifier = Modifier,
    @RawRes res: Int,
    animationState: LottieAnimationState
) {
    val animationSpec = remember { LottieAnimationSpec.RawRes(res) }
    LottieAnimation(
        animationSpec,
        modifier = modifier,
        animationState
    )
}
