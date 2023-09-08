package com.sd.lib.compose.gesture

import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyInputModifierNode
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.fClick(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = composed {
    clickable(
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        indication = indication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

fun Modifier.fCombinedClick(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: ((Offset) -> Unit)? = null,
    onDoubleClick: ((Offset) -> Unit)? = null,
    onClick: ((Offset) -> Unit)? = null,
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "fCombinedClick"
        properties["indication"] = indication
        properties["interactionSource"] = interactionSource
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
        properties["onDoubleClick"] = onDoubleClick
        properties["onLongClick"] = onLongClick
        properties["onLongClickLabel"] = onLongClickLabel
    }
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Modifier
        .indication(interactionSource, indication)
        .hoverable(enabled = enabled, interactionSource = interactionSource)
        .then(
            CombinedClickableElement(
                interactionSource,
                enabled,
                onClickLabel,
                role,
                onClick,
                onLongClickLabel,
                onLongClick,
                onDoubleClick
            )
        )
}

private class CombinedClickableElement(
    private val interactionSource: MutableInteractionSource,
    private val enabled: Boolean,
    private val onClickLabel: String?,
    private val role: Role? = null,
    private val onClick: ((Offset) -> Unit)?,
    private val onLongClickLabel: String?,
    private val onLongClick: ((Offset) -> Unit)?,
    private val onDoubleClick: ((Offset) -> Unit)?
) : ModifierNodeElement<CombinedClickableNode>() {
    override fun create() = CombinedClickableNode(
        interactionSource,
        enabled,
        onClickLabel,
        role,
        onClick,
        onLongClickLabel,
        onLongClick,
        onDoubleClick
    )

    override fun update(node: CombinedClickableNode) {
        node.update(
            interactionSource,
            enabled,
            onClickLabel,
            role,
            onClick,
            onLongClickLabel,
            onLongClick,
            onDoubleClick
        )
    }

    // Defined in the factory functions with inspectable
    override fun InspectorInfo.inspectableProperties() = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CombinedClickableElement

        if (interactionSource != other.interactionSource) return false
        if (enabled != other.enabled) return false
        if (onClickLabel != other.onClickLabel) return false
        if (role != other.role) return false
        if (onClick != other.onClick) return false
        if (onLongClickLabel != other.onLongClickLabel) return false
        if (onLongClick != other.onLongClick) return false
        if (onDoubleClick != other.onDoubleClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionSource.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + (onClickLabel?.hashCode() ?: 0)
        result = 31 * result + (role?.hashCode() ?: 0)
        result = 31 * result + onClick.hashCode()
        result = 31 * result + (onLongClickLabel?.hashCode() ?: 0)
        result = 31 * result + (onLongClick?.hashCode() ?: 0)
        result = 31 * result + (onDoubleClick?.hashCode() ?: 0)
        return result
    }
}

private class CombinedClickableNode(
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onClickLabel: String?,
    role: Role?,
    onClick: ((Offset) -> Unit)?,
    onLongClickLabel: String?,
    private var onLongClick: ((Offset) -> Unit)?,
    onDoubleClick: ((Offset) -> Unit)?
) : AbstractClickableNode(interactionSource, enabled, onClickLabel, role, onClick) {
    override val clickableSemanticsNode = delegate(
        ClickableSemanticsNode(
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            onClick = onClick,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick
        )
    )

    override val clickablePointerInputNode = delegate(
        CombinedClickablePointerInputNode(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            interactionData = interactionData,
            onLongClick,
            onDoubleClick
        )
    )

    fun update(
        interactionSource: MutableInteractionSource,
        enabled: Boolean,
        onClickLabel: String?,
        role: Role?,
        onClick: ((Offset) -> Unit)?,
        onLongClickLabel: String?,
        onLongClick: ((Offset) -> Unit)?,
        onDoubleClick: ((Offset) -> Unit)?
    ) {
        // If we have gone from no long click to having a long click or vice versa,
        // cancel any existing press interactions.
        if ((this.onLongClick == null) != (onLongClick == null)) {
            disposeInteractionSource()
        }
        this.onLongClick = onLongClick
        updateCommon(interactionSource, enabled, onClickLabel, role, onClick)
        clickableSemanticsNode.update(
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            onClick = onClick,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick
        )
        clickablePointerInputNode.update(
            enabled = enabled,
            interactionSource = interactionSource,
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick
        )
    }
}

private sealed class AbstractClickableNode(
    private var interactionSource: MutableInteractionSource,
    private var enabled: Boolean,
    private var onClickLabel: String?,
    private var role: Role?,
    private var onClick: ((Offset) -> Unit)?
) : DelegatingNode(), PointerInputModifierNode, KeyInputModifierNode {
    abstract val clickablePointerInputNode: AbstractClickablePointerInputNode
    abstract val clickableSemanticsNode: ClickableSemanticsNode

    class InteractionData {
        val currentKeyPressInteractions = mutableMapOf<Key, PressInteraction.Press>()
        var pressInteraction: PressInteraction.Press? = null
        var centreOffset: Offset = Offset.Zero
    }

    protected val interactionData = InteractionData()

    protected fun updateCommon(
        interactionSource: MutableInteractionSource,
        enabled: Boolean,
        onClickLabel: String?,
        role: Role? = null,
        onClick: ((Offset) -> Unit)?,
    ) {
        if (this.interactionSource != interactionSource) {
            disposeInteractionSource()
            this.interactionSource = interactionSource
        }
        if (this.enabled != enabled) {
            if (!enabled) {
                disposeInteractionSource()
            }
            this.enabled = enabled
        }
        this.onClickLabel = onClickLabel
        this.role = role
        this.onClick = onClick
    }

    override fun onDetach() {
        disposeInteractionSource()
    }

    protected fun disposeInteractionSource() {
        interactionData.pressInteraction?.let { oldValue ->
            val interaction = PressInteraction.Cancel(oldValue)
            interactionSource.tryEmit(interaction)
        }
        interactionData.currentKeyPressInteractions.values.forEach {
            interactionSource.tryEmit(PressInteraction.Cancel(it))
        }
        interactionData.pressInteraction = null
        interactionData.currentKeyPressInteractions.clear()
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        clickablePointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        clickablePointerInputNode.onCancelPointerInput()
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        return when {
            enabled && event.isPress -> {
                // If the key already exists in the map, keyEvent is a repeat event.
                // We ignore it as we only want to emit an interaction for the initial key press.
                if (!interactionData.currentKeyPressInteractions.containsKey(event.key)) {
                    val press = PressInteraction.Press(interactionData.centreOffset)
                    interactionData.currentKeyPressInteractions[event.key] = press
                    coroutineScope.launch { interactionSource.emit(press) }
                    true
                } else {
                    false
                }
            }

            enabled && event.isClick -> {
                if (onClick != null) {
                    interactionData.currentKeyPressInteractions.remove(event.key)?.let {
                        coroutineScope.launch {
                            interactionSource.emit(PressInteraction.Release(it))
                        }
                    }
                    onClick?.invoke(Offset.Zero)
                    true
                } else {
                    false
                }
            }

            else -> false
        }
    }

    override fun onPreKeyEvent(event: KeyEvent) = false
}

private class ClickableSemanticsElement(
    private val enabled: Boolean,
    private val role: Role?,
    private val onLongClickLabel: String?,
    private val onLongClick: ((Offset) -> Unit)?,
    private val onClickLabel: String?,
    private val onClick: (Offset) -> Unit
) : ModifierNodeElement<ClickableSemanticsNode>() {
    override fun create() = ClickableSemanticsNode(
        enabled = enabled,
        role = role,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick,
        onClickLabel = onClickLabel,
        onClick = onClick
    )

    override fun update(node: ClickableSemanticsNode) {
        node.update(enabled, onClickLabel, role, onClick, onLongClickLabel, onLongClick)
    }

    override fun InspectorInfo.inspectableProperties() = Unit

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + onLongClickLabel.hashCode()
        result = 31 * result + onLongClick.hashCode()
        result = 31 * result + onClickLabel.hashCode()
        result = 31 * result + onClick.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClickableSemanticsElement) return false

        if (enabled != other.enabled) return false
        if (role != other.role) return false
        if (onLongClickLabel != other.onLongClickLabel) return false
        if (onLongClick != other.onLongClick) return false
        if (onClickLabel != other.onClickLabel) return false
        if (onClick != other.onClick) return false

        return true
    }
}

private class ClickableSemanticsNode(
    private var enabled: Boolean,
    private var onClickLabel: String?,
    private var role: Role?,
    private var onClick: ((Offset) -> Unit)?,
    private var onLongClickLabel: String?,
    private var onLongClick: ((Offset) -> Unit)?,
) : SemanticsModifierNode, Modifier.Node() {
    fun update(
        enabled: Boolean,
        onClickLabel: String?,
        role: Role?,
        onClick: ((Offset) -> Unit)?,
        onLongClickLabel: String?,
        onLongClick: ((Offset) -> Unit)?,
    ) {
        this.enabled = enabled
        this.onClickLabel = onClickLabel
        this.role = role
        this.onClick = onClick
        this.onLongClickLabel = onLongClickLabel
        this.onLongClick = onLongClick
    }

    override val shouldMergeDescendantSemantics: Boolean
        get() = true

    override fun SemanticsPropertyReceiver.applySemantics() {
        if (this@ClickableSemanticsNode.role != null) {
            role = this@ClickableSemanticsNode.role!!
        }
        if (onClick != null) {
            onClick(
                action = { onClick?.invoke(Offset.Zero); true },
                label = onClickLabel
            )
        }
        if (onLongClick != null) {
            onLongClick(
                action = { onLongClick?.invoke(Offset.Zero); true },
                label = onLongClickLabel
            )
        }
        if (!enabled) {
            disabled()
        }
    }
}

private sealed class AbstractClickablePointerInputNode(
    protected var enabled: Boolean,
    protected var interactionSource: MutableInteractionSource?,
    protected var onClick: ((Offset) -> Unit)?,
    protected val interactionData: AbstractClickableNode.InteractionData
) : DelegatingNode(), ModifierLocalModifierNode, CompositionLocalConsumerModifierNode,
    PointerInputModifierNode {

    private val delayPressInteraction = {
        isComposeRootInScrollableContainer()
    }

    private val pointerInputNode = delegate(SuspendingPointerInputModifierNode { pointerInput() })

    protected abstract suspend fun PointerInputScope.pointerInput()

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    protected suspend fun PressGestureScope.handlePressInteraction(offset: Offset) {
        interactionSource?.let { interactionSource ->
            handlePressInteraction(
                offset,
                interactionSource,
                interactionData,
                delayPressInteraction
            )
        }
    }

    protected fun resetPointerInputHandler() = pointerInputNode.resetPointerInputHandler()
}

private class CombinedClickablePointerInputNode(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: ((Offset) -> Unit)?,
    interactionData: AbstractClickableNode.InteractionData,
    private var onLongClick: ((Offset) -> Unit)?,
    private var onDoubleClick: ((Offset) -> Unit)?
) : AbstractClickablePointerInputNode(
    enabled,
    interactionSource,
    onClick,
    interactionData
) {
    override suspend fun PointerInputScope.pointerInput() {
        interactionData.centreOffset = size.center.toOffset()
        detectTapGestures(
            onDoubleTap = if (enabled && onDoubleClick != null) {
                { onDoubleClick?.invoke(it) }
            } else null,
            onLongPress = if (enabled && onLongClick != null) {
                { onLongClick?.invoke(it) }
            } else null,
            onPress = { offset ->
                if (enabled) {
                    handlePressInteraction(offset)
                }
            },
            onTap = if (enabled && onClick != null) {
                { onClick?.invoke(it) }
            } else null
        )
    }

    fun update(
        enabled: Boolean,
        interactionSource: MutableInteractionSource,
        onClick: ((Offset) -> Unit)?,
        onLongClick: ((Offset) -> Unit)?,
        onDoubleClick: ((Offset) -> Unit)?
    ) {
        // These are captured inside callbacks, not as an input to detectTapGestures,
        // so no need need to reset pointer input handling
        this.onClick = onClick
        this.interactionSource = interactionSource

        var changed = false

        // This is captured as a parameter to detectTapGestures, so we need to restart detecting
        // gestures if it changes.
        if (this.enabled != enabled) {
            this.enabled = enabled
            changed = true
        }

        // We capture these inside the callback, so if the lambda changes value we don't want to
        // reset input handling - only reset if they go from not-defined to defined, and vice-versa,
        // as that is what is captured in the parameter to detectTapGestures.
        if ((this.onLongClick == null) != (onLongClick == null)) {
            changed = true
        }
        this.onLongClick = onLongClick
        if ((this.onDoubleClick == null) != (onDoubleClick == null)) {
            changed = true
        }
        this.onDoubleClick = onDoubleClick
        if (changed) resetPointerInputHandler()
    }
}

private suspend fun PressGestureScope.handlePressInteraction(
    pressPoint: Offset,
    interactionSource: MutableInteractionSource,
    interactionData: AbstractClickableNode.InteractionData,
    delayPressInteraction: () -> Boolean
) {
    coroutineScope {
        val delayJob = launch {
            if (delayPressInteraction()) {
                delay(TapIndicationDelay)
            }
            val press = PressInteraction.Press(pressPoint)
            interactionSource.emit(press)
            interactionData.pressInteraction = press
        }
        val success = tryAwaitRelease()
        if (delayJob.isActive) {
            delayJob.cancelAndJoin()
            // The press released successfully, before the timeout duration - emit the press
            // interaction instantly. No else branch - if the press was cancelled before the
            // timeout, we don't want to emit a press interaction.
            if (success) {
                val press = PressInteraction.Press(pressPoint)
                val release = PressInteraction.Release(press)
                interactionSource.emit(press)
                interactionSource.emit(release)
            }
        } else {
            interactionData.pressInteraction?.let { pressInteraction ->
                val endInteraction = if (success) {
                    PressInteraction.Release(pressInteraction)
                } else {
                    PressInteraction.Cancel(pressInteraction)
                }
                interactionSource.emit(endInteraction)
            }
        }
        interactionData.pressInteraction = null
    }
}

/**
 * How long to wait before appearing 'pressed' (emitting [PressInteraction.Press]) - if a touch
 * down will quickly become a drag / scroll, this timeout means that we don't show a press effect.
 */
private val TapIndicationDelay: Long = ViewConfiguration.getTapTimeout().toLong()

/**
 * Returns whether the root Compose layout node is hosted in a scrollable container outside of
 * Compose. On Android this will be whether the root View is in a scrollable ViewGroup, as even if
 * nothing in the Compose part of the hierarchy is scrollable, if the View itself is in a scrollable
 * container, we still want to delay presses in case presses in Compose convert to a scroll outside
 * of Compose.
 *
 * Combine this with [ModifierLocalScrollableContainer], which returns whether a [Modifier] is
 * within a scrollable Compose layout, to calculate whether this modifier is within some form of
 * scrollable container, and hence should delay presses.
 */
private fun CompositionLocalConsumerModifierNode.isComposeRootInScrollableContainer(): Boolean {
    return currentValueOf(LocalView).isInScrollableViewGroup()
}

private fun View.isInScrollableViewGroup(): Boolean {
    var p = parent
    while (p != null && p is ViewGroup) {
        if (p.shouldDelayChildPressedState()) {
            return true
        }
        p = p.parent
    }
    return false
}

/**
 * Whether the specified [KeyEvent] should trigger a press for a clickable component.
 */
private val KeyEvent.isPress: Boolean
    get() = type == KeyEventType.KeyDown && isEnter

/**
 * Whether the specified [KeyEvent] should trigger a click for a clickable component.
 */
private val KeyEvent.isClick: Boolean
    get() = type == KeyEventType.KeyUp && isEnter

private val KeyEvent.isEnter: Boolean
    get() = when (key.nativeKeyCode) {
        KEYCODE_DPAD_CENTER, KEYCODE_ENTER, KEYCODE_NUMPAD_ENTER -> true
        else -> false
    }