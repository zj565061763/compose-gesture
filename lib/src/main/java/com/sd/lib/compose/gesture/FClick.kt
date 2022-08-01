package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.fOnClick(
    requireUnconsumed: Boolean = true,
    time: Long = 150L,
    block: () -> Unit
) = fOnClickTime(
    requireUnconsumed = requireUnconsumed,
) {
    if (it <= time) block()
}

fun Modifier.fOnClickTime(
    requireUnconsumed: Boolean = true,
    block: (time: Long) -> Unit
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val firstDown = awaitFirstDown(requireUnconsumed = requireUnconsumed)
            val event = awaitPointerEvent()
            event.changes.any {
                if (it.id == firstDown.id && it.changedToUp(requireUnconsumed)) {
                    val time = it.uptimeMillis - it.previousUptimeMillis
                    block(time)
                    true
                } else false
            }
        }
    }
}