package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity

fun Modifier.fPointerChange(
    requireUnconsumedDown: Boolean = false,
    requireUnconsumedUp: Boolean = false,
    requireUnconsumedMove: Boolean = true,
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
                val firstDown = fAwaitDown(
                    count = 1,
                    requireUnconsumed = requireUnconsumedDown,
                    pass = PointerEventPass.Main,
                ).first()

                scopeImpl.onStart(currentEvent)
                onStart?.invoke(scopeImpl)
                if (scopeImpl.isGestureCanceled) return@awaitPointerEventScope

                scopeImpl.onDown(firstDown, currentEvent)
                onDown?.invoke(scopeImpl, firstDown)
                if (scopeImpl.isGestureCanceled) return@awaitPointerEventScope

                // Used for move event.
                val touchSlop = viewConfiguration.touchSlop
                var pastTouchSlop = false
                var pan = Offset.Zero

                do {
                    val event = awaitPointerEvent()
                    val hasDown = event.fHasDownPointer()
                    event.changes.forEach {
                        if (it.changedToDown(requireUnconsumedDown)) {
                            scopeImpl.onDown(it, currentEvent)
                            onDown?.invoke(scopeImpl, it)
                        } else if (it.changedToUp(requireUnconsumedUp)) {
                            scopeImpl.onUp(it, currentEvent)
                            onUp?.invoke(scopeImpl, it)
                        } else if (it.positionChanged(requireUnconsumedMove)) {
                            if (hasDown) {
                                if (!pastTouchSlop) {
                                    pan += event.calculatePan()
                                    if (pan.getDistance() > touchSlop) {
                                        pastTouchSlop = true
                                    }
                                }
                                if (pastTouchSlop) {
                                    scopeImpl.onMove(it, currentEvent)
                                    onMove?.invoke(scopeImpl, it)
                                }
                            }
                        }
                    }
                    if (scopeImpl.isGestureCanceled) return@awaitPointerEventScope
                } while (hasDown)

                onFinish?.invoke(scopeImpl)
            }
        }
    }
}

interface FPointerChangeScope : FGestureScope {
    val currentEvent: PointerEvent?

    val pointerCount: Int

    val maxPointerCount: Int

    var enableVelocity: Boolean

    fun getPointerVelocity(pointerId: PointerId): Velocity
}

private class FPointerChangeScopeImpl : BaseGestureScope(), FPointerChangeScope {
    private var _currentEvent: PointerEvent? = null
    private var _maxPointerCount = 0

    private val _pointerHolder = mutableMapOf<PointerId, PointerInfo>()

    override val currentEvent: PointerEvent?
        get() = _currentEvent

    override val pointerCount: Int
        get() = _pointerHolder.size

    override val maxPointerCount: Int
        get() = _maxPointerCount

    override var enableVelocity: Boolean = false

    override fun getPointerVelocity(pointerId: PointerId): Velocity {
        return _pointerHolder[pointerId]?.velocityTracker?.calculateVelocity() ?: Velocity.Zero
    }

    fun onStart(event: PointerEvent) {
        reset()
        resetCancelFlag()
        _currentEvent = event
    }

    fun onDown(input: PointerInputChange, event: PointerEvent) {
        if (_pointerHolder.containsKey(input.id)) return

        val velocityTracker = if (enableVelocity) {
            VelocityTracker().apply { this.addPosition(input.uptimeMillis, input.position) }
        } else null

        _pointerHolder[input.id] = PointerInfo(input, velocityTracker)
        _maxPointerCount++
        _currentEvent = event
    }

    fun onUp(input: PointerInputChange, event: PointerEvent) {
        _pointerHolder.remove(input.id)
        _currentEvent = event
    }

    fun onMove(input: PointerInputChange, event: PointerEvent) {
        if (enableVelocity) {
            _pointerHolder[input.id]?.velocityTracker?.addPosition(input.uptimeMillis, input.position)
        }
        _currentEvent = event
    }

    override fun cancelGesture() {
        super.cancelGesture()
        reset()
    }

    private fun reset() {
        _currentEvent = null
        _maxPointerCount = 0
        _pointerHolder.clear()
        enableVelocity = false
    }

    private data class PointerInfo(
        val change: PointerInputChange,
        val velocityTracker: VelocityTracker?,
    )
}

