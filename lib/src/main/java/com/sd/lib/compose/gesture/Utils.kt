package com.sd.lib.compose.gesture

import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
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

/** 是否有被消费的移动事件 */
fun PointerEvent.fHasConsumedPositionChange(): Boolean {
  return changes.fastAny { it.fIsConsumedPositionChange() }
}

/** 是否被消费的移动事件 */
fun PointerInputChange.fIsConsumedPositionChange(): Boolean {
  return isConsumed && positionChangedIgnoreConsumed()
}