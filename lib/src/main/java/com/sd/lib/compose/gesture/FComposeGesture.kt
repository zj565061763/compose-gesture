package com.sd.lib.compose.gesture

import android.util.Log
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*

fun Modifier.fOnPointerChange(
    requireUnconsumed: Boolean = true,
    onDownFirst: ((PointerInputChange) -> Unit)? = null,
    onUpAll: (maxDownCount: Int) -> Unit
) = pointerInput(Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val firstDown = awaitFirstDown(requireUnconsumed = requireUnconsumed)
            onDownFirst?.invoke(firstDown)

            var maxDownCount = 1
            while (true) {
                val event = awaitPointerEvent()
                event.changes.forEach {
                    if (it.changedToDown(requireUnconsumed)) {
                        maxDownCount++
                    }
                }
                if (!event.fHasPointerDown()) break
            }

            onUpAll(maxDownCount)
        }
    }
}

fun Modifier.fOnPointerDownChange(
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    block: (List<PointerInputChange>) -> Unit
) = pointerInput(Unit) {
    val pointerHolder = mutableMapOf<PointerId, PointerInputChange>()
    awaitPointerEventScope {
        while (true) {
            val oldCount = pointerHolder.size
            val event = awaitPointerEvent(pass = pass)
            event.fillDownMap(requireUnconsumed = requireUnconsumed, map = pointerHolder)
            if (oldCount != pointerHolder.size) {
                block(pointerHolder.values.toList())
            }
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
        }
        if (it.changedToUp(requireUnconsumed)) {
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

internal inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("FComposeGesture", msg)
}