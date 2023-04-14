package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
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
        awaitEachGesture {
            val touchSlop = viewConfiguration.touchSlop
            var pastTouchSlop = false
            var pan = Offset.Zero

            scopeImpl.reset()
            while (true) {
                val event = awaitPointerEvent(pass)
                scopeImpl.setCurrentEvent(event)

                var hasDown = false
                for (input in event.changes) {
                    if (input.pressed) hasDown = true
                    when {
                        input.fChangedToDown(requireUnconsumedDown) -> {
                            if (scopeImpl.maxPointerCount == 0) {
                                onStart?.invoke(scopeImpl)
                                if (scopeImpl.isGestureCanceled) break
                            }
                            scopeImpl.onDown(input)
                            onDown?.invoke(scopeImpl, input)
                        }

                        input.fChangedToUp(requireUnconsumedUp) -> {
                            onUp?.invoke(scopeImpl, input)
                            scopeImpl.onUpAfter(input)
                        }

                        input.fPositionChanged(requireUnconsumedMove) -> {
                            if (!pastTouchSlop) {
                                pan += event.calculatePan()
                                if (pan.getDistance() > touchSlop) {
                                    pastTouchSlop = true
                                }
                            }
                            if (pastTouchSlop) {
                                scopeImpl.onMove(input)
                                onMove?.invoke(scopeImpl, input)
                            }
                        }
                    }
                }

                if (scopeImpl.isGestureCanceled) break
                if (!hasDown) break
            }

            onFinish?.invoke(scopeImpl)
        }
    }
}

interface FPointerChangeScope : FGestureScope {
    /** 当前触摸点的数量 */
    val pointerCount: Int

    /** 触摸点最多时候的数量 */
    val maxPointerCount: Int

    /** 是否开启速率监测 */
    var enableVelocity: Boolean

    /** 获取某个触摸点的速率 */
    fun getPointerVelocity(pointerId: PointerId): Velocity?
}

private class FPointerChangeScopeImpl : BaseGestureScope(), FPointerChangeScope {
    private var _maxPointerCount = 0
    private val _pointerHolder = mutableMapOf<PointerId, PointerInfo>()

    override val pointerCount: Int
        get() = _pointerHolder.size

    override val maxPointerCount: Int
        get() = _maxPointerCount

    override var enableVelocity: Boolean = false

    override fun getPointerVelocity(pointerId: PointerId): Velocity? {
        return _pointerHolder[pointerId]?.velocityTracker?.calculateVelocity()
    }

    fun onDown(input: PointerInputChange) {
        if (_pointerHolder.containsKey(input.id)) return

        val velocityTracker = if (enableVelocity) {
            VelocityTracker().apply { this.addPosition(input.uptimeMillis, input.position) }
        } else null

        _pointerHolder[input.id] = PointerInfo(input, velocityTracker)
        _pointerHolder.size.let { count ->
            if (_maxPointerCount < count) {
                _maxPointerCount = count
            }
        }
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

