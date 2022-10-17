package com.sd.lib.compose.gesture

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

                scopeImpl.onStart()
                onStart?.invoke(scopeImpl)
                if (scopeImpl.isGestureCanceled) return@awaitPointerEventScope

                scopeImpl.onDown(firstDown)
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
                            scopeImpl.onDown(it)
                            onDown?.invoke(scopeImpl, it)
                        } else if (it.changedToUp(requireUnconsumedUp)) {
                            scopeImpl.onUp(it)
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
                                    scopeImpl.onMove(it)
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
    val downPointerCount: Int

    val maxDownPointerCount: Int

    var enableVelocity: Boolean

    fun getPointerVelocity(pointerId: PointerId): Velocity
}

internal class FPointerChangeScopeImpl : BaseGestureScope(), FPointerChangeScope {
    private val _downPointers = mutableMapOf<PointerId, PointerInputChange>()
    private val _downPointersVelocity = mutableMapOf<PointerId, VelocityTracker>()

    private var _maxDownPointerCount = 0
    private var _enableVelocity = false

    override val downPointerCount: Int
        get() = _downPointers.size

    override val maxDownPointerCount: Int
        get() = _maxDownPointerCount

    override var enableVelocity: Boolean by ::_enableVelocity

    override fun getPointerVelocity(pointerId: PointerId): Velocity {
        val velocityTracker = _downPointersVelocity[pointerId] ?: return Velocity.Zero
        return velocityTracker.calculateVelocity()
    }

    internal fun onStart() {
        reset()
        resetCancelFlag()
    }

    internal fun onDown(input: PointerInputChange) {
        if (_downPointers.put(input.id, input) == null) {
            _maxDownPointerCount++
        }
        if (_enableVelocity) {
            if (!_downPointersVelocity.containsKey(input.id)) {
                _downPointersVelocity[input.id] = VelocityTracker().also {
                    it.addPosition(input.uptimeMillis, input.position)
                }
            }
        }
    }

    internal fun onUp(input: PointerInputChange) {
        _downPointers.remove(input.id)
    }

    internal fun onMove(input: PointerInputChange) {
        if (_enableVelocity) {
            _downPointersVelocity[input.id]?.addPosition(input.uptimeMillis, input.position)
        }
    }

    override fun cancelGesture() {
        super.cancelGesture()
        reset()
    }

    private fun reset() {
        _downPointers.clear()
        _downPointersVelocity.clear()
        _maxDownPointerCount = 0
    }
}