package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointer

class SampleGestureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Sample()
            }
        }
    }
}

@Composable
private fun Sample(
    modifier: Modifier = Modifier,
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .fPointer(
                onStart = {
                    calculatePan = true
                    calculateZoom = true
                    calculateRotation = true
                },
                onCalculate = {
                    logMsg { "onCalculate pan:${this.pan} zoom:${this.zoom} rotation:${this.rotation} centroid:${this.centroid}" }

                    if ((scale < 0.3f && zoom < 1f) || (scale > 5f && zoom > 1f)) {
                        cancelPointer()
                        return@fPointer
                    }

                    offset += this.pan
                    scale *= this.zoom
                    rotation += this.rotation
                },
            ),
        contentAlignment = Alignment.Center,
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
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                }
        )
    }
}