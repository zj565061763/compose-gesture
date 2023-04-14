package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.*
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
import kotlin.math.PI
import kotlin.math.abs

fun Modifier.fPointerChange(
    requireUnconsumedDown: Boolean = false,
    requireUnconsumedUp: Boolean = false,
    requireUnconsumedMove: Boolean = false,
    pass: PointerEventPass = PointerEventPass.Main,
    onStart: (FPointerChangeScope.() -> Unit)? = null,
    onDown: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onUp: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onMove: (FPointerChangeScope.(PointerInputChange) -> Unit)? = null,
    onCalculate: (FPointerChangeScope.() -> Unit)? = null,
    onFinish: (FPointerChangeScope.() -> Unit)? = null,
) = composed {

    val scopeImpl = remember { FPointerChangeScopeImpl() }

    pointerInput(Unit) {
        awaitEachGesture {
            val touchSlop = viewConfiguration.touchSlop
            var pastTouchSlop = false
            var pan = Offset.Zero
            var zoom = 1f
            var rotation = 0f

            var started = false
            var calculatePan = false
            var calculateZoom = false
            var calculateRotation = false

            scopeImpl.reset()
            while (true) {
                val event = awaitPointerEvent(pass)
                scopeImpl.setCurrentEvent(event)

                if (started) {
                    if (calculatePan) {
                        val change = event.calculatePan()
                        if (!pastTouchSlop) {
                            pan += change
                            val panMotion = pan.getDistance()
                            if (panMotion > touchSlop) pastTouchSlop = true
                        }
                        if (pastTouchSlop) {
                            scopeImpl.setPan(change)
                        }
                    }

                    if (calculateZoom || calculateRotation) {
                        val centroidSize = if (pastTouchSlop) 0f else event.calculateCentroidSize(useCurrent = false)
                        if (calculateZoom) {
                            val change = event.calculateZoom()
                            if (!pastTouchSlop) {
                                zoom *= change
                                val zoomMotion = abs(1 - zoom) * centroidSize
                                if (zoomMotion > touchSlop) pastTouchSlop = true
                            }
                            if (pastTouchSlop) {
                                scopeImpl.setZoom(change)
                            }
                        }
                        if (calculateRotation) {
                            val change = event.calculateRotation()
                            if (!pastTouchSlop) {
                                rotation += change
                                val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                                if (rotationMotion > touchSlop) pastTouchSlop = true
                            }
                            if (pastTouchSlop) {
                                scopeImpl.setRotation(change)
                            }
                        }
                    }

                    if (calculatePan && (scopeImpl.pan != Offset.Zero) ||
                        calculateZoom && (scopeImpl.zoom != 1f) ||
                        calculateRotation && (scopeImpl.rotation != 0f)
                    ) {
                        onCalculate?.invoke(scopeImpl)
                    }
                }

                var hasDown = false
                for (input in event.changes) {
                    if (input.pressed) hasDown = true
                    when {
                        input.fChangedToDown(requireUnconsumedDown) -> {
                            if (!started) {
                                started = true
                                onStart?.invoke(scopeImpl)
                                if (scopeImpl.isGestureCanceled) break
                                calculatePan = scopeImpl.calculatePan
                                calculateZoom = scopeImpl.calculateZoom
                                calculateRotation = scopeImpl.calculateRotation
                            }
                            scopeImpl.onDown(input)
                            onDown?.invoke(scopeImpl, input)
                        }

                        input.fChangedToUp(requireUnconsumedUp) -> {
                            if (started) {
                                onUp?.invoke(scopeImpl, input)
                                scopeImpl.onUpAfter(input)
                            }
                        }

                        input.fPositionChanged(requireUnconsumedMove) -> {
                            if (started) {
                                if (pastTouchSlop) {
                                    scopeImpl.onMove(input)
                                    onMove?.invoke(scopeImpl, input)
                                }
                            }
                        }
                    }
                }

                if (scopeImpl.isGestureCanceled) break
                if (!hasDown) break
            }

            if (started) {
                onFinish?.invoke(scopeImpl)
            }
        }
    }
}

interface FPointerChangeScope : FGestureScope {
    /** 当前触摸点的数量 */
    val pointerCount: Int

    /** 触摸点最多时候的数量 */
    val maxPointerCount: Int

    /** 两次事件之间移动的距离 */
    val pan: Offset

    /** 两次事件之间移动的缩放 */
    val zoom: Float

    /** 两次事件之间的旋转 */
    val rotation: Float

    /** 是否开启速率监测 */
    var enableVelocity: Boolean

    /** 是否计算[pan] */
    var calculatePan: Boolean

    /** 是否计算[zoom] */
    var calculateZoom: Boolean

    /** 是否计算[rotation] */
    var calculateRotation: Boolean

    /** 获取某个触摸点的速率 */
    fun getPointerVelocity(pointerId: PointerId): Velocity?
}

private class FPointerChangeScopeImpl : BaseGestureScope(), FPointerChangeScope {
    private val _pointerHolder = mutableMapOf<PointerId, PointerInfo>()
    private var _maxPointerCount = 0
    private var _pan = Offset.Zero
    private var _zoom = 1f
    private var _rotation = 0f

    override val pointerCount: Int get() = _pointerHolder.size
    override val maxPointerCount: Int get() = _maxPointerCount
    override val pan: Offset get() = _pan
    override val zoom: Float get() = _zoom
    override val rotation: Float get() = _rotation

    override var enableVelocity: Boolean = false
    override var calculatePan: Boolean = false
    override var calculateZoom: Boolean = false
    override var calculateRotation: Boolean = false

    override fun getPointerVelocity(pointerId: PointerId): Velocity? {
        return _pointerHolder[pointerId]?.velocityTracker?.calculateVelocity()
    }

    fun setPan(value: Offset) {
        _pan = value
    }

    fun setZoom(value: Float) {
        _zoom = value
    }

    fun setRotation(value: Float) {
        _rotation = value
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
        _pointerHolder.clear()
        _maxPointerCount = 0
        enableVelocity = false
        _pan = Offset.Zero
        _zoom = 1f
        _rotation = 0f
    }

    private data class PointerInfo(
        val change: PointerInputChange,
        val velocityTracker: VelocityTracker?,
    )
}