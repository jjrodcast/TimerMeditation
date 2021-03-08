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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    private var timer: Timer? = null

    private val _modelState = MutableLiveData<TimerModel>()
    val modelState get(): LiveData<TimerModel> = _modelState

    init {
        _modelState.value = TimerModel()
    }

    private fun getCurrentModel() = _modelState.value!!

    fun createTimer() {
        timer = Timer(
            DEFAULT_TIMER,
            onTickTimer = { currentMillis, timerFormat ->
                val model = getCurrentModel()
                _modelState.value = model.copy(
                    timeInMillis = currentMillis,
                    timeWithFormat = timerFormat,
                    state = State.RUNNING
                )
            },
            onTickerFinish = {
                val model = getCurrentModel()

                _modelState.value = model.copy(
                    timeInMillis = 0L,
                    timeWithFormat = TIMER_ENDS,
                    state = State.FINISHED
                )
            }
        )
        timer?.start()
    }

    fun stopTimer() {
        timer?.cancel()
        val model = getCurrentModel()
        _modelState.value = model.copy(state = State.IDLE)
    }

    fun resetTime() {
        _modelState.value = TimerModel()
    }
}
