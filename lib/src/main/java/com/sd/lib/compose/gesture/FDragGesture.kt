package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.fOnDrag(
    onFinish: (() -> Unit)? = null,
    onDrag: (event: PointerEvent, change: Offset) -> Unit,
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val touchSlop = viewConfiguration.touchSlop
            var pastTouchSlop = false
            var pan = Offset.Zero
            var hasDrag = false

            awaitFirstDown(requireUnconsumed = false)
            while (true) {
                val event = awaitPointerEvent()

                if (!event.fHasPointerDown() || event.fHasConsumed()) {
                    if (hasDrag) onFinish?.invoke()
                    break
                }

                val panChange = event.calculatePan()
                if (!pastTouchSlop) {
                    pan += panChange

                    val panMotion = pan.getDistance()
                    if (panMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    if (panChange != Offset.Zero) {
                        hasDrag = true
                        onDrag(event, panChange)
                    }
                }
            }
        }
    }
}