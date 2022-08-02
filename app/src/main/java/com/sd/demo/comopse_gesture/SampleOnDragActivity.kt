package com.sd.demo.comopse_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fHasConsumed
import com.sd.lib.compose.gesture.fOnDrag

class SampleOnDragActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SampleOnDrag()
                }
            }
        }
    }
}

@Composable
fun SampleOnDrag(
    modifier: Modifier = Modifier,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .fOnDrag { event, change ->
                logMsg { "drag event:${event.fHasConsumed()} change:$change" }
                offset += change
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.scale),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offset.x
                    translationY = offset.y
                }
        )
    }
}