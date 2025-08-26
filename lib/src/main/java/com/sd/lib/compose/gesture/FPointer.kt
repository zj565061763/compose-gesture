package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.fastAny
import kotlin.math.PI
import kotlin.math.abs

fun Modifier.fPointer(
  pass: PointerEventPass = PointerEventPass.Main,
  touchSlop: Float? = null,
  sharePointerInputWithSiblings: Boolean = false,
  onStart: (FPointerStartScope.() -> Unit)? = null,
  onDown: (FPointerScope.(PointerInputChange) -> Unit)? = null,
  onUp: (FPointerScope.(PointerInputChange) -> Unit)? = null,
  onMove: (FPointerScope.(PointerInputChange) -> Unit)? = null,
  onCalculate: (FPointerScope.() -> Unit)? = null,
  onFinish: (FPointerScope.() -> Unit)? = null,
) = this then FPointerElement(
  pass = pass,
  touchSlop = touchSlop,
  sharePointerInputWithSiblings = sharePointerInputWithSiblings,
  onStart = onStart,
  onDown = onDown,
  onUp = onUp,
  onMove = onMove,
  onCalculate = onCalculate,
  onFinish = onFinish,
)

private class FPointerElement(
  private var pass: PointerEventPass,
  private var touchSlop: Float?,
  private var sharePointerInputWithSiblings: Boolean,
  private var onStart: (FPointerStartScope.() -> Unit)?,
  private var onDown: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onUp: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onMove: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onCalculate: (FPointerScope.() -> Unit)?,
  private var onFinish: (FPointerScope.() -> Unit)?,
) : ModifierNodeElement<FPointerNode>() {
  override fun create(): FPointerNode {
    return FPointerNode(
      pass = pass,
      touchSlop = touchSlop,
      sharePointerInputWithSiblings = sharePointerInputWithSiblings,
      onStart = onStart,
      onDown = onDown,
      onUp = onUp,
      onMove = onMove,
      onCalculate = onCalculate,
      onFinish = onFinish,
    )
  }

  override fun update(node: FPointerNode) {
    node.update(
      pass = pass,
      touchSlop = touchSlop,
      sharePointerInputWithSiblings = sharePointerInputWithSiblings,
      onStart = onStart,
      onDown = onDown,
      onUp = onUp,
      onMove = onMove,
      onCalculate = onCalculate,
      onFinish = onFinish,
    )
  }

  override fun hashCode(): Int {
    var result = pass.hashCode()
    result = 31 * result + touchSlop.hashCode()
    result = 31 * result + sharePointerInputWithSiblings.hashCode()
    result = 31 * result + onStart.hashCode()
    result = 31 * result + onDown.hashCode()
    result = 31 * result + onUp.hashCode()
    result = 31 * result + onMove.hashCode()
    result = 31 * result + onCalculate.hashCode()
    result = 31 * result + onFinish.hashCode()
    return result
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FPointerElement) return false
    return pass == other.pass &&
      touchSlop == other.touchSlop &&
      sharePointerInputWithSiblings == other.sharePointerInputWithSiblings &&
      onStart == other.onStart &&
      onDown == other.onDown &&
      onUp == other.onUp &&
      onMove == other.onMove &&
      onCalculate == other.onCalculate &&
      onFinish == other.onFinish
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "fPointer"
    properties["pass"] = pass
    properties["touchSlop"] = touchSlop
    properties["sharePointerInputWithSiblings"] = sharePointerInputWithSiblings
    properties["onStart"] = onStart
    properties["onDown"] = onDown
    properties["onUp"] = onUp
    properties["onMove"] = onMove
    properties["onCalculate"] = onCalculate
    properties["onFinish"] = onFinish
  }
}

private class FPointerNode(
  private var pass: PointerEventPass,
  private var touchSlop: Float?,
  private var sharePointerInputWithSiblings: Boolean,
  private var onStart: (FPointerStartScope.() -> Unit)?,
  private var onDown: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onUp: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onMove: (FPointerScope.(PointerInputChange) -> Unit)?,
  private var onCalculate: (FPointerScope.() -> Unit)?,
  private var onFinish: (FPointerScope.() -> Unit)?,
) : DelegatingNode(), PointerInputModifierNode {

  private val pointerInputNode = delegate(SuspendingPointerInputModifierNode { pointerInput() })

  fun update(
    pass: PointerEventPass,
    touchSlop: Float?,
    sharePointerInputWithSiblings: Boolean,
    onStart: (FPointerStartScope.() -> Unit)?,
    onDown: (FPointerScope.(PointerInputChange) -> Unit)?,
    onUp: (FPointerScope.(PointerInputChange) -> Unit)?,
    onMove: (FPointerScope.(PointerInputChange) -> Unit)?,
    onCalculate: (FPointerScope.() -> Unit)?,
    onFinish: (FPointerScope.() -> Unit)?,
  ) {
    this.pass = pass
    this.touchSlop = touchSlop
    this.sharePointerInputWithSiblings = sharePointerInputWithSiblings
    this.onStart = onStart
    this.onDown = onDown
    this.onUp = onUp
    this.onMove = onMove
    this.onCalculate = onCalculate
    this.onFinish = onFinish
  }

  override fun onPointerEvent(
    pointerEvent: PointerEvent,
    pass: PointerEventPass,
    bounds: IntSize,
  ) {
    pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
  }

  override fun onCancelPointerInput() {
    pointerInputNode.onCancelPointerInput()
  }

  override fun sharePointerInputWithSiblings(): Boolean {
    return sharePointerInputWithSiblings
  }

  private suspend fun PointerInputScope.pointerInput() {
    awaitEachGesture {
      val scopeImpl = FPointerScopeImpl(this)

      val touchSlop = touchSlop ?: viewConfiguration.touchSlop
      var pastTouchSlop = false
      var pan = Offset.Zero
      var zoom = 1f
      var rotation = 0f

      var started = false
      var calculatePan = false
      var calculateZoom = false
      var calculateRotation = false

      while (true) {
        val event = awaitPointerEvent(pass)

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

          if (pastTouchSlop) {
            val centroid = event.calculateCentroid(useCurrent = false)
            scopeImpl.setCentroid(centroid)
            if (
              (calculatePan && scopeImpl.pan != Offset.Zero) ||
              (calculateZoom && scopeImpl.zoom != 1f) ||
              (calculateRotation && scopeImpl.rotation != 0f)
            ) {
              onCalculate?.invoke(scopeImpl)
            }
          }
        }

        if (scopeImpl.isCanceledPointer) break

        for (input in event.changes) {
          when {
            input.changedToDownIgnoreConsumed() -> {
              if (!started) {
                started = true

                val startScopeImpl = FPointerStartScopeImpl(this)
                onStart?.invoke(startScopeImpl)
                if (scopeImpl.isCanceledPointer) break

                calculatePan = startScopeImpl.calculatePan
                calculateZoom = startScopeImpl.calculateZoom
                calculateRotation = startScopeImpl.calculateRotation
              }
              scopeImpl.onDownBefore(input)
              onDown?.invoke(scopeImpl, input)
            }

            input.changedToUpIgnoreConsumed() -> {
              if (started) {
                onUp?.invoke(scopeImpl, input)
                scopeImpl.onUpAfter(input)
              }
            }

            input.positionChangedIgnoreConsumed() -> {
              if (started) {
                onMove?.invoke(scopeImpl, input)
              }
            }
          }
          if (scopeImpl.isCanceledPointer) break
        }

        if (scopeImpl.isCanceledPointer) break
        if (!event.changes.fastAny { it.pressed }) break
      }

      if (started) {
        onFinish?.invoke(scopeImpl)
      }
    }
  }
}

interface FPointerScope : AwaitPointerEventScope {
  /** 当前触摸点的数量 */
  val pointerCount: Int
  /** 触摸点最多时候的数量 */
  val maxPointerCount: Int

  /** 两次事件之间的距离 */
  val pan: Offset
  /** 两次事件之间的缩放 */
  val zoom: Float
  /** 两次事件之间的旋转 */
  val rotation: Float
  /** 触摸点的中心点 */
  val centroid: Offset

  /** 是否被取消[cancelPointer] */
  val isCanceledPointer: Boolean

  /** 添加速率信息 */
  fun velocityAdd(change: PointerInputChange)

  /** 获取某个触摸点的速率 */
  fun velocityGet(pointerId: PointerId): Velocity?

  /** 取消触摸事件监听 */
  fun cancelPointer()
}

interface FPointerStartScope : AwaitPointerEventScope {
  /** 是否计算距离 */
  var calculatePan: Boolean
  /** 是否计算缩放 */
  var calculateZoom: Boolean
  /** 是否计算旋转 */
  var calculateRotation: Boolean
}

private class FPointerStartScopeImpl(
  awaitPointerEventScope: AwaitPointerEventScope,
) : FPointerStartScope, AwaitPointerEventScope by awaitPointerEventScope {
  override var calculatePan: Boolean = false
  override var calculateZoom: Boolean = false
  override var calculateRotation: Boolean = false
}

private class FPointerScopeImpl(
  awaitPointerEventScope: AwaitPointerEventScope,
) : FPointerScope, AwaitPointerEventScope by awaitPointerEventScope {
  private var _isCanceled = false

  private val _pointerHolder = mutableMapOf<PointerId, PointerInfo>()
  private var _maxPointerCount = 0

  private var _pan = Offset.Zero
  private var _zoom = 1f
  private var _rotation = 0f
  private var _centroid = Offset.Zero

  override val pointerCount: Int get() = _pointerHolder.size
  override val maxPointerCount: Int get() = _maxPointerCount

  override val pan: Offset get() = _pan
  override val zoom: Float get() = _zoom
  override val rotation: Float get() = _rotation
  override val centroid: Offset get() = _centroid

  override val isCanceledPointer: Boolean get() = _isCanceled

  override fun velocityAdd(change: PointerInputChange) {
    val info = _pointerHolder[change.id] ?: return
    info.getOrCreateVelocityTracker().addPointerInputChange(change)
  }

  override fun velocityGet(pointerId: PointerId): Velocity? {
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

  fun setCentroid(value: Offset) {
    _centroid = value
  }

  fun onDownBefore(input: PointerInputChange) {
    if (_pointerHolder.containsKey(input.id)) return
    _pointerHolder[input.id] = PointerInfo(null)
    val count = _pointerHolder.size
    if (count > _maxPointerCount) {
      _maxPointerCount = count
    }
  }

  fun onUpAfter(input: PointerInputChange) {
    _pointerHolder.remove(input.id)
  }

  override fun cancelPointer() {
    _isCanceled = true
  }

  private data class PointerInfo(
    var velocityTracker: VelocityTracker?,
  ) {
    fun getOrCreateVelocityTracker(): VelocityTracker {
      return velocityTracker ?: VelocityTracker().also { velocityTracker = it }
    }
  }
}