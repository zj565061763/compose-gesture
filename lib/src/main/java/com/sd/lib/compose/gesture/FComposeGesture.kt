package com.sd.lib.compose.gesture

import android.util.Log
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*

fun Modifier.fOnPointerChange(
    requireUnconsumed: Boolean = true,
    onStart: ((PointerInputChange) -> Unit)? = null,
    onDown: ((PointerInputChange) -> Unit)? = null,
    onUp: ((PointerInputChange) -> Unit)? = null,
    onMove: ((PointerInputChange) -> Unit)? = null,
    onFinish: () -> Unit
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val firstDown = awaitFirstDown(requireUnconsumed = requireUnconsumed)
            onStart?.invoke(firstDown)
            onDown?.invoke(firstDown)

            while (true) {
                val event = awaitPointerEvent()
                val hasDown = event.fHasPointerDown()
                event.changes.forEach {
                    if (it.changedToDown(requireUnconsumed)) {
                        onDown?.invoke(it)
                    } else if (it.changedToUp(requireUnconsumed)) {
                        onUp?.invoke(it)
                    } else if (it.positionChanged(requireUnconsumed)) {
                        if (hasDown) {
                            onMove?.invoke(it)
                        }
                    }
                }
                if (!hasDown) break
            }

            onFinish()
        }
    }
}

suspend fun AwaitPointerEventScope.fAwaitDowns(
    count: Int = 2,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
): List<PointerInputChange> {
    require(count > 1)
    val pointerHolder = mutableMapOf<PointerId, PointerInputChange>()
    while (pointerHolder.size < count) {
        val event = awaitPointerEvent(pass = pass)
        event.fillDownMap(requireUnconsumed = requireUnconsumed, map = pointerHolder)
    }
    return pointerHolder.values.toList()
}

suspend fun AwaitPointerEventScope.fAwaitAllPointersUp() {
    if (currentEvent.fHasPointerDown()) {
        do {
            val event = awaitPointerEvent(PointerEventPass.Final)
        } while (event.fHasPointerDown())
    }
}

fun PointerEvent.fHasPointerDown(): Boolean = changes.any { it.pressed }

private fun PointerEvent.fillDownMap(
    requireUnconsumed: Boolean,
    map: MutableMap<PointerId, PointerInputChange>,
) {
    changes.forEach {
        if (it.changedToDown(requireUnconsumed)) {
            map[it.id] = it
        } else if (it.changedToUp(requireUnconsumed)) {
            map.remove(it.id)
        }
    }
}

private fun PointerInputChange.changedToDown(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToDown() else changedToDownIgnoreConsumed()
}

private fun PointerInputChange.changedToUp(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToUp() else changedToUpIgnoreConsumed()
}

private fun PointerInputChange.positionChanged(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) positionChanged() else positionChangedIgnoreConsumed()
}

internal inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("FComposeGesture", msg)
}