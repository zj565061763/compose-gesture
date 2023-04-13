package com.sd.lib.compose.gesture

import android.util.Log
import androidx.annotation.CallSuper
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.*

suspend fun AwaitPointerEventScope.fAwaitFirstDown(
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main
): PointerInputChange {
    return awaitFirstDown(
        requireUnconsumed = requireUnconsumed,
        pass = pass,
    )
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
        if (!it.isConsumed) {
            it.consume()
            consume = true
        }
    }
    return consume
}

fun PointerEvent.fConsumePositionChanged(requireUnconsumed: Boolean = true): Boolean {
    var consume = false
    changes.forEach {
        if (it.fPositionChanged(requireUnconsumed)) {
            it.consume()
            consume = true
        }
    }
    return consume
}

internal fun PointerInputChange.fChangedToDown(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToDown() else changedToDownIgnoreConsumed()
}

internal fun PointerInputChange.fChangedToUp(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) changedToUp() else changedToUpIgnoreConsumed()
}

internal fun PointerInputChange.fPositionChanged(requireUnconsumed: Boolean): Boolean {
    return if (requireUnconsumed) positionChanged() else positionChangedIgnoreConsumed()
}

interface FGestureScope {
    val currentEvent: PointerEvent?

    fun cancelGesture()
}

internal open class BaseGestureScope : FGestureScope {
    private var _currentEvent: PointerEvent? = null

    var isGestureCanceled = false
        private set

    final override val currentEvent: PointerEvent?
        get() = _currentEvent

    final override fun cancelGesture() {
        isGestureCanceled = true
    }

    @CallSuper
    open fun reset() {
        _currentEvent = null
        isGestureCanceled = false
    }

    fun setCurrentEvent(event: PointerEvent?) {
        _currentEvent = event
    }
}

internal inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture", msg)
}