package com.sd.lib.compose.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.util.fastAny

suspend fun AwaitPointerEventScope.fAwaitAllPointersUp(
  pass: PointerEventPass = PointerEventPass.Final,
) {
  if (currentEvent.fHasPointerPressed()) {
    do {
      val event = awaitPointerEvent(pass)
    } while (event.fHasPointerPressed())
  }
}

fun PointerEvent.fHasPointerPressed(): Boolean {
  return changes.fastAny { it.pressed }
}

fun PointerEvent.fHasConsumedPositionChange(): Boolean {
  return changes.any { it.fIsConsumedPositionChange() }
}

fun PointerInputChange.fIsConsumedPositionChange(): Boolean {
  return isConsumed && (position - previousPosition) != Offset.Zero
}