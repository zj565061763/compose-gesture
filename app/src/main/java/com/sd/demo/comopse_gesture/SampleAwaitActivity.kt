package com.sd.demo.comopse_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fAwaitAllPointersUp
import com.sd.lib.compose.gesture.fAwaitDowns

class SampleAwaitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SampleAwait()
                }
            }
        }
    }
}

@Composable
private fun SampleAwait(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        fAwaitDowns(count = 2)
                        logMsg { "fAwaitDowns" }

                        fAwaitAllPointersUp()
                        logMsg { "fAwaitAllPointersUp" }
                    }
                }
            }
    )
}