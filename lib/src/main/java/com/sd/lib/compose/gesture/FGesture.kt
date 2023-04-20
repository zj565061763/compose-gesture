package com.sd.lib.compose.gesture

import android.util.Log
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed

fun Modifier.fClick(
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onPress: (suspend PressGestureScope.(Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
) = pointerInput(Unit) {
    detectTapGestures(
        onDoubleTap = onDoubleTap,
        onLongPress = onLongPress,
        onPress = onPress ?: {},
        onTap = onTap,
    )
}

suspend fun AwaitPointerEventScope.fAwaitFirstDown(
    requireUnconsumed: Boolean = true, pass: PointerEventPass = PointerEventPass.Main
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

fun PointerEvent.fConsumePositionChanged(): Boolean {
    var consume = false
    changes.forEach {
        if (it.positionChanged()) {
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

internal inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture", msg)
}