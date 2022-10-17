package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SamplePointerChange()
                }
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
                    logMsg { "PointerChange onStart event:$currentEvent changes:${currentEvent?.changes?.size}" }
                },
                onDown = {
                    logMsg { "PointerChange onDown pointerCount:$pointerCount id:${it.id} event:$currentEvent changes:${currentEvent?.changes?.size}" }
                },
                onUp = {
                    logMsg { "PointerChange onUp pointerCount:$pointerCount id:${it.id} event:$currentEvent changes:${currentEvent?.changes?.size}" }
                },
                onMove = {
                    logMsg { "PointerChange onMove id:${it.id} event:$currentEvent changes:${currentEvent?.changes?.size}" }
                },
                onFinish = {
                    logMsg { "PointerChange onFinish maxPointerCount:$maxPointerCount" }
                },
            )
    )
}