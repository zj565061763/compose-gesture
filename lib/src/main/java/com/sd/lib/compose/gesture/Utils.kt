package com.sd.lib.compose.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.util.fastAny

/** 等待所有指针都抬起 */
suspend fun AwaitPointerEventScope.fAwaitAllPointersUp(
  pass: PointerEventPass = PointerEventPass.Final,
) {
  if (currentEvent.fHasPointerPressed()) {
    do {
      val event = awaitPointerEvent(pass)
    } while (event.fHasPointerPressed())
  }
}

/** 是否有指针按下 */
fun PointerEvent.fHasPointerPressed(): Boolean {
  return changes.fastAny { it.pressed }
}

fun PointerEvent.fHasConsumedPositionChange(): Boolean {
  return changes.fastAny { it.fIsConsumedPositionChange() }
}

fun PointerInputChange.fIsConsumedPositionChange(): Boolean {
  return isConsumed && ((position - previousPosition) != Offset.Zero)
}