package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

fun Modifier.fScaleGesture(
    requireUnconsumedDown: Boolean = false,
    onStart: (FScaleGestureScope.() -> Unit)? = null,
    onFinish: (FScaleGestureScope.() -> Unit)? = null,
    onScale: FScaleGestureScope.(event: PointerEvent, centroid: Offset, change: Float) -> Unit,
) = composed {

    val scopeImpl = remember { FScaleGestureScopeImpl() }

    pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = requireUnconsumedDown)
                scopeImpl.onStart(currentEvent)

                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var zoom = 1f
                var hasScale = false

                while (!scopeImpl.isGestureCanceled) {
                    val event = awaitPointerEvent()

                    if (hasScale) {
                        if (event.fHasConsumed() || event.fDownPointerCount() < 2) {
                            onFinish?.invoke(scopeImpl)
                            break
                        }
                    }

                    val zoomChange = event.calculateZoom()
                    if (!pastTouchSlop) {
                        zoom *= zoomChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        if (zoomMotion > touchSlop) {
                            pastTouchSlop = true
                        }
                    }

                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        if (zoomChange != 1f) {
                            if (!hasScale) {
                                onStart?.invoke(scopeImpl)
                                if (scopeImpl.isGestureCanceled) break
                            }

                            hasScale = true
                            scopeImpl.onScale(event, centroid, zoomChange)
                        }
                    }
                }
            }
        }
    }
}

interface FScaleGestureScope : FGestureScope {
}

private class FScaleGestureScopeImpl : BaseGestureScope(), FScaleGestureScope {

    fun onStart(event: PointerEvent) {
        reset()
        resetCancelFlag()
        setCurrentEvent(event)
    }

    fun reset() {

    }
}