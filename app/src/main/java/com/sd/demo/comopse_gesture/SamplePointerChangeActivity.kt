package com.sd.demo.comopse_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
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
                    logMsg { "onPointerChange onStart" }
                },
                onDown = {
                    logMsg { "onPointerChange onDown count:$downPointerCount" }
                },
                onUp = {
                    logMsg { "onPointerChange onUp count:$downPointerCount" }
                },
                onMove = {
                    logMsg { "onPointerChange onMove" }
                },
                onFinish = {
                    logMsg { "onPointerChange onFinish maxCount:$maxDownPointerCount" }
                },
            )
    )
}