package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

fun Modifier.fScaleGesture(
    requireUnconsumedDown: Boolean = false,
    onStart: (FScaleGestureScope.() -> Unit)? = null,
    onFinish: (FScaleGestureScope.() -> Unit)? = null,
    onScale: FScaleGestureScope.(centroid: Offset, change: Float) -> Unit,
) = composed {

    val scopeImpl = remember { FScaleGestureScopeImpl() }

    pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = requireUnconsumedDown)

                scopeImpl.resetCancelFlag()
                scopeImpl.setCurrentEvent(currentEvent)

                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var zoom = 1f
                var hasScale = false

                do {
                    val event = awaitPointerEvent()

                    if (hasScale) {
                        if (event.fHasConsumed() || event.fDownPointerCount() < 2) {
                            scopeImpl.setCurrentEvent(currentEvent)
                            onFinish?.invoke(scopeImpl)
                            break
                        }
                    }

                    if (!event.fHasDownPointer()) break
                    val zoomChange = event.calculateZoom()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        if (zoomMotion > touchSlop) {
                            pastTouchSlop = true
                        }
                    }

                    if (pastTouchSlop && zoomChange != 1f) {
                        if (!hasScale) {
                            scopeImpl.setCurrentEvent(currentEvent)
                            onStart?.invoke(scopeImpl)
                            if (scopeImpl.isGestureCanceled) break
                            hasScale = true
                        }

                        scopeImpl.setCurrentEvent(currentEvent)
                        val centroid = event.calculateCentroid(useCurrent = false)
                        onScale.invoke(scopeImpl, centroid, zoomChange)
                    }

                } while (!scopeImpl.isGestureCanceled)
            }
        }
    }
}

interface FScaleGestureScope : FGestureScope {
}

private class FScaleGestureScopeImpl : BaseGestureScope(), FScaleGestureScope {
}