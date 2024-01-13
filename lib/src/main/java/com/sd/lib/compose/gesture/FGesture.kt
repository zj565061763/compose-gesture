package com.sd.lib.compose.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange

suspend fun AwaitPointerEventScope.fAwaitAllPointersUp() {
    if (currentEvent.fHasDownPointer()) {
        do {
            val event = awaitPointerEvent(PointerEventPass.Final)
        } while (event.fHasDownPointer())
    }
}

fun PointerEvent.fHasDownPointer(): Boolean = changes.any { it.pressed }

fun PointerEvent.fConsume(
    predicate: (PointerInputChange) -> Boolean,
): Boolean {
    var consume = false
    changes.forEach {
        if (predicate(it)) {
            it.consume()
            consume = true
        }
    }
    return consume
}

/**
 * true表示当前位置发生了变化，并且被消费了
 */
fun PointerInputChange.fIsConsumedPositionChange(): Boolean {
    return isConsumed && (position - previousPosition) != Offset.Zero
}