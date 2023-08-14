package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointer

class SamplePointerActivity : ComponentActivity() {
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .fPointer(
                onStart = {
                    enableVelocity = true
                    calculatePan = true
                    calculateZoom = true
                    calculateRotation = true
                    logMsg { "onStart event:$currentEvent" }
                },
                onDown = {
                    logMsg { "onDown pointerCount:$pointerCount id:${it.id}" }
                    if (pointerCount >= 5) {
                        cancelGesture()
                    }
                },
                onUp = {
                    logMsg { "onUp pointerCount:$pointerCount id:${it.id} velocity:${getPointerVelocity(it.id)}" }
                },
                onMove = {
                    logMsg { "onMove pointerCount:$pointerCount id:${it.id}" }
                },
                onCalculate = {
                    logMsg { "onCalculate pan:$pan zoom:$zoom rotation:$rotation" }
                },
                onFinish = {
                    logMsg { "onFinish maxPointerCount:$maxPointerCount" }
                },
            )
    )
}