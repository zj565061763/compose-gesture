package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SamplePointerChange()
            }
        }
    }
}

@Composable
private fun SamplePointerChange(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fPointerChange(
                onStart = {
                    logMsg { "PointerChange onStart event:$currentEvent" }
                },
                onDown = {
                    logMsg { "PointerChange onDown pointerCount:$pointerCount id:${it.id} changes:${currentEvent?.changes?.size} event:$currentEvent" }
                    if (pointerCount >= 4) {
                        cancelGesture()
                    }
                },
                onUp = {
                    logMsg { "PointerChange onUp pointerCount:$pointerCount id:${it.id} changes:${currentEvent?.changes?.size} event:$currentEvent " }
                },
                onMove = {
                    logMsg { "PointerChange onMove id:${it.id} changes:${currentEvent?.changes?.size} event:$currentEvent" }
                },
                onFinish = {
                    logMsg { "PointerChange onFinish maxPointerCount:$maxPointerCount" }
                },
            )
    )
}