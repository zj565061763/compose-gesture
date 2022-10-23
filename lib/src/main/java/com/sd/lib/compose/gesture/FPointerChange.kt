package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity

fun Modifier.fPointerChange(
    requireUnconsumedDown: Boolean = false,
    requireUnconsumedUp: Boolean = false,
    requireUnconsumedMove: Boolean = false,
    pass: PointerEventPass = PointerEventPass.Main,
    onStart: (FPointerChangeScope.() -> Unit)? = null,
    onDown: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onUp: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onMove: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onFinish: (FPointerChangeScope.() -> Unit)? = null,
) = composed {

    val scopeImpl = remember { FPointerChangeScopeImpl() }

    pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                val firstDown = awaitFirstDown(requireUnconsumed = requireUnconsumedDown)

                scopeImpl.reset()
                scopeImpl.setCurrentEvent(currentEvent)

                onStart?.invoke(scopeImpl)
                if (scopeImpl.isGestureCanceled) {
                    onFinish?.invoke(scopeImpl)
                    return@awaitPointerEventScope
                }

                scopeImpl.onDown(firstDown)
                onDown?.invoke(scopeImpl, firstDown)
                if (scopeImpl.isGestureCanceled) {
                    onFinish?.invoke(scopeImpl)
                    return@awaitPointerEventScope
                }

                // Used for move event.
                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var pan = Offset.Zero

                do {
                    val event = awaitPointerEvent(pass)
                    scopeImpl.setCurrentEvent(event)
                    val hasDown = event.fHasDownPointer()

                    event.changes.forEach {
                        if (it.fChangedToDown(requireUnconsumedDown)) {
                            scopeImpl.onDown(it)
                            onDown?.invoke(scopeImpl, it)
                        } else if (it.fChangedToUp(requireUnconsumedUp)) {
                            onUp?.invoke(scopeImpl, it)
                            scopeImpl.onUpAfter(it)
                        } else if (it.fPositionChanged(requireUnconsumedMove)) {
                            if (hasDown) {
                                if (!pastTouchSlop) {
                                    pan += event.calculatePan()
                                    if (pan.getDistance() > touchSlop) {
                                        pastTouchSlop = true
                                    }
                                }
                                if (pastTouchSlop) {
                                    scopeImpl.onMove(it)
                                    onMove?.invoke(scopeImpl, it)
                                }
                            }
                        }
                    }

                    if (scopeImpl.isGestureCanceled) break
                } while (hasDown)

                onFinish?.invoke(scopeImpl)
            }
        }
    }
}

interface FPointerChangeScope : FGestureScope {
    val pointerCount: Int

    val maxPointerCount: Int

    var enableVelocity: Boolean

    fun getPointerVelocity(pointerId: PointerId): Velocity
}

private class FPointerChangeScopeImpl : BaseGestureScope(), FPointerChangeScope {
    private var _maxPointerCount = 0
    private val _pointerHolder = mutableMapOf<PointerId, PointerInfo>()

    override val pointerCount: Int
        get() = _pointerHolder.size

    override val maxPointerCount: Int
        get() = _maxPointerCount

    override var enableVelocity: Boolean = false

    override fun getPointerVelocity(pointerId: PointerId): Velocity {
        return _pointerHolder[pointerId]?.velocityTracker?.calculateVelocity() ?: Velocity.Zero
    }

    fun onDown(input: PointerInputChange) {
        if (_pointerHolder.containsKey(input.id)) return

        val velocityTracker = if (enableVelocity) {
            VelocityTracker().apply { this.addPosition(input.uptimeMillis, input.position) }
        } else null

        _pointerHolder[input.id] = PointerInfo(input, velocityTracker)
        _maxPointerCount++
    }

    fun onUpAfter(input: PointerInputChange) {
        _pointerHolder.remove(input.id)
    }

    fun onMove(input: PointerInputChange) {
        if (enableVelocity) {
            _pointerHolder[input.id]?.velocityTracker?.addPosition(input.uptimeMillis, input.position)
        }
    }

    override fun reset() {
        super.reset()
        _maxPointerCount = 0
        _pointerHolder.clear()
        enableVelocity = false
    }

    private data class PointerInfo(
        val change: PointerInputChange,
        val velocityTracker: VelocityTracker?,
    )
}

