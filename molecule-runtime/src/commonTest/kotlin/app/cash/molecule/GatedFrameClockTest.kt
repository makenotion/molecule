/*
 * Copyright (C) 2023 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.molecule

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThan
import assertk.assertions.isPositive
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class GatedFrameClockTest {
  @Test
  fun resumingWithQueuedFrameRespectsPause() = runTest {
    val frameClock = GatedFrameClock(backgroundScope, EmptyCoroutineContext)
    val frames = Channel<Int>(1)
    launch(UnconfinedTestDispatcher(testScheduler)) {
      repeat(2) { frame ->
        frameClock.withFrameNanos {
          frameClock.isRunning = false
          frames.trySend(frame).getOrThrow()
        }
      }
    }

    frameClock.isRunning = false
    frameClock.isRunning = true
    runCurrent()
    assertThat(frames.receive()).isEqualTo(0)

    frameClock.isRunning = true
    assertThat(frames.receive()).isEqualTo(1)
  }

  @Test
  fun ticksWithTime() = runTest {
    val frameClock = GatedFrameClock(backgroundScope, EmptyCoroutineContext)
    val frameTimeA = frameClock.withFrameNanos { it }
    val frameTimeB = frameClock.withFrameNanos { it }
    assertThat(frameTimeA).all {
      isPositive()
      isLessThan(frameTimeB)
    }
  }
}
