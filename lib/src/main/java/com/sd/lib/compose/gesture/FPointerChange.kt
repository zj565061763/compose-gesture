package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity

fun Modifier.fOnPointerChange(
    requireUnconsumed: Boolean = true,
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
                val firstDown = awaitFirstDown(requireUnconsumed = requireUnconsumed)

                scopeImpl.onStart()
                onStart?.invoke(scopeImpl)

                scopeImpl.onDown(firstDown)
                onDown?.invoke(scopeImpl, firstDown)

                while (true) {
                    val event = awaitPointerEvent()
                    val hasDown = event.fHasPointerDown()
                    event.changes.forEach {
                        if (it.changedToDown(requireUnconsumed)) {
                            scopeImpl.onDown(it)
                            onDown?.invoke(scopeImpl, it)
                        } else if (it.changedToUp(requireUnconsumed)) {
                            scopeImpl.onUp(it)
                            onUp?.invoke(scopeImpl, it)
                        } else if (it.positionChanged(requireUnconsumed)) {
                            if (hasDown) {
                                scopeImpl.onMove(it)
                                onMove?.invoke(scopeImpl, it)
                            }
                        }
                    }
                    if (!hasDown) break
                }

                onFinish?.invoke(scopeImpl)
            }
        }
    }
}

interface FPointerChangeScope {
    val downPointerCount: Int

    val maxDownPointerCount: Int

    var enableVelocity: Boolean

    fun getPointerVelocity(pointerId: PointerId): Velocity
}

internal class FPointerChangeScopeImpl() : FPointerChangeScope {
    private val _downPointers = mutableMapOf<PointerId, PointerInputChange>()
    private val _downPointersVelocity = mutableMapOf<PointerId, VelocityTracker>()

    private var _maxDownPointerCount = 0
    private var _enableVelocity = false

    override val downPointerCount: Int
        get() = _downPointers.size

    override val maxDownPointerCount: Int
        get() = _maxDownPointerCount

    override var enableVelocity: Boolean
        get() = _enableVelocity
        set(value) {
            _enableVelocity = value
        }

    override fun getPointerVelocity(pointerId: PointerId): Velocity {
        val velocityTracker = _downPointersVelocity[pointerId] ?: return Velocity.Zero
        return velocityTracker.calculateVelocity()
    }

    internal fun onStart() {
        _downPointers.clear()
        _downPointersVelocity.clear()
        _maxDownPointerCount = 0
    }

    internal fun onDown(input: PointerInputChange) {
        if (_downPointers.put(input.id, input) == null) {
            _maxDownPointerCount++
        }
        if (_enableVelocity) {
            if (!_downPointersVelocity.containsKey(input.id)) {
                _downPointersVelocity[input.id] = VelocityTracker()
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
}