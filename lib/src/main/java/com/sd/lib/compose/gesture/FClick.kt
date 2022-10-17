package com.sd.lib.compose.gesture

import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.fClick(
    onPress: (suspend PressGestureScope.(Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
    onDoubleTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
) = pointerInput(Unit) {
    detectTapGestures(
        onPress = onPress ?: {},
        onTap = onTap,
        onDoubleTap = onDoubleTap,
        onLongPress = onLongPress,
    )
}