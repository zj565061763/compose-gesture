package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeVelocityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleVelocityTracker()
                }
            }
        }
    }
}

@Composable
private fun SampleVelocityTracker(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fPointerChange(
                onStart = {
                    enableVelocity = true
                },
                onUp = {
                    logMsg { "onUp ${it.id} ${getPointerVelocity(it.id)}" }
                },
            )
    )
}