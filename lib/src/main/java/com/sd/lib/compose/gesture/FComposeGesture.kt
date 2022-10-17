package com.sd.lib.compose.gesture

import android.util.Log
import androidx.compose.ui.input.pointer.*

suspend fun AwaitPointerEventScope.fAwaitDown(
    count: Int = 1,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
): List<PointerInputChange> {
    require(count > 0) { "Require count > 0" }
    val map = mutableMapOf<PointerId, PointerInputChange>()
    do {
        val event = awaitPointerEvent(pass = pass)
        event.changes.forEach {
            if (it.changedToDown(requireUnconsumed)) {
                map[it.id] = it
            } else if (it.changedToUp(requireUnconsumed)) {
                map.remove(it.id)
            }
        }
    } while (map.size < count)
    return map.values.toList()
}

suspend fun AwaitPointerEventScope.fAwaitAllPointersUp() {
    if (currentEvent.fHasDownPointer()) {
        do {
            val event = awaitPointerEvent(PointerEventPass.Final)
        } while (event.fHasDownPointer())
    }
}

fun PointerEvent.fHasDownPointer(): Boolean = changes.any { it.pressed }

fun PointerEvent.fDownPointerCount(): Int = changes.fold(0) { acc, input ->
    acc + (if (input.pressed) 1 else 0)
}

fun PointerEvent.fHasConsumed(): Boolean = changes.any { it.isConsumed }

fun PointerEvent.fConsume(): Boolean {
    var consume = false
    changes.forEach {
        if (!it.isConsumed) consume = true
        it.consume()
    }
    return consume
}

internal fun PointerInputChange.changedToDown(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToDown() else changedToDownIgnoreConsumed()
}

internal fun PointerInputChange.changedToUp(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToUp() else changedToUpIgnoreConsumed()
}

internal fun PointerInputChange.positionChanged(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) positionChanged() else positionChangedIgnoreConsumed()
}

interface FGestureScope {
    fun cancelGesture()
}

internal open class BaseGestureScope : FGestureScope {
    internal var isGestureCanceled = false
        private set

    override fun cancelGesture() {
        isGestureCanceled = true
    }

    internal fun resetCancelFlag() {
        isGestureCanceled = false
    }
}

internal inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("FComposeGesture", msg)
}