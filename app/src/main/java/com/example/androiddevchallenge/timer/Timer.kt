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

import android.os.CountDownTimer

const val DEFAULT_TIMER = 600000L
const val DEFAULT_TIMER_STRING = "10:00"
const val TIMER_ENDS = "00:00"
const val DEFAULT_INTERVAL = 10L

class Timer(
    initialMilliseconds: Long = DEFAULT_TIMER,
    interval: Long = DEFAULT_INTERVAL,
    private val onTickTimer: (timeLeftMillis: Long, timeLeftFormat: String) -> Unit,
    private val onTickerFinish: () -> Unit
) : CountDownTimer(initialMilliseconds, interval) {

    override fun onFinish() {
        onTickerFinish()
    }

    override fun onTick(millisUntilFinished: Long) {
        val seconds = (millisUntilFinished / 1000) % 60
        val minutes = (millisUntilFinished / 1000 / 60) % 60

        val minutesString = "${if (minutes < 10) "0$minutes" else minutes}"
        val secondsString = "${if (seconds < 10) "0$seconds" else seconds}"

        onTickTimer(millisUntilFinished, "$minutesString:$secondsString")
    }
}
