package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.fOnDrag(
    onFinish: (() -> Unit)? = null,
    onDrag: FDragGestureScope.(event: PointerEvent, change: Offset) -> Unit,
) = composed {

    val scopeImpl = remember { FDragGestureScopeImpl() }

    pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var pan = Offset.Zero
                var hasDrag = false

                scopeImpl.resetCancelFlag()
                awaitFirstDown(requireUnconsumed = false)

                while (!scopeImpl.isGestureCanceled) {
                    val event = awaitPointerEvent(pass = PointerEventPass.Main)

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
                            scopeImpl.onDrag(event, panChange)
                        }
                    }
                }
            }
        }
    }
}

interface FDragGestureScope : FGestureScope {
}

private class FDragGestureScopeImpl : BaseGestureScope(), FDragGestureScope {
}