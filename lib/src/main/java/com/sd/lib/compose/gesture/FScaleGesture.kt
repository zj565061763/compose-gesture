package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

fun Modifier.fOnScale(
    onScale: (centroid: Offset, zoomChange: Float) -> Unit,
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val touchSlop = viewConfiguration.touchSlop
            var pastTouchSlop = false
            var zoom = 1f

            awaitFirstDown(requireUnconsumed = false)
            while (true) {
                val event = awaitPointerEvent()
                if (!event.fHasPointerDown()) break

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
                        onScale(centroid, zoomChange)
                        event.fConsume()
                    }
                }
            }
        }
    }
}