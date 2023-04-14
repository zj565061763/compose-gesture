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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fConsumePositionChanged
import com.sd.lib.compose.gesture.fScaleGesture

class SampleScaleActivity : ComponentActivity() {
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
    var scale by remember { mutableStateOf(1f) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .fScaleGesture(
                onStart = {
                    logMsg { "scale onStart" }
                },
                onFinish = {
                    logMsg { "scale onFinish" }
                }
            ) { centroid, change ->
                logMsg { "scale centroid:$centroid change:$change" }
                scale *= change
                currentEvent?.fConsumePositionChanged()

                if (scale < 0.3f && change < 1f) {
                    cancelGesture()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.scale),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
        )
    }
}