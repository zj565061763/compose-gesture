package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

fun Modifier.fScaleGesture(
    onStart: (FScaleGestureScope.() -> Unit)? = null,
    onFinish: (FScaleGestureScope.() -> Unit)? = null,
    onScale: FScaleGestureScope.(centroid: Offset, change: Float) -> Unit,
) = composed {

    val scopeImpl = remember { FScaleGestureScopeImpl() }

    pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                scopeImpl.reset()

                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var zoom = 1f
                var hasScale = false

                do {
                    val event = awaitPointerEvent()
                    scopeImpl.setCurrentEvent(event)

                    val pointerCount = event.fDownPointerCount()

                    if (hasScale) {
                        if (pointerCount < 2 || event.fHasConsumed()) {
                            onFinish?.invoke(scopeImpl)
                            break
                        }
                    }

                    if (pointerCount <= 0) break

                    if (pointerCount > 1) {
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
                                onStart?.invoke(scopeImpl)
                                if (scopeImpl.isGestureCanceled) break
                                hasScale = true
                            }

                            val centroid = event.calculateCentroid(useCurrent = false)
                            onScale.invoke(scopeImpl, centroid, zoomChange)
                        }
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