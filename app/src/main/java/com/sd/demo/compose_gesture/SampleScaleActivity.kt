package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.comopse_gesture.R
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fConsumePositionChanged
import com.sd.lib.compose.gesture.fScaleGesture

class SampleScaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleScale()
                }
            }
        }
    }
}

@Composable
fun SampleScale(
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableStateOf(1f) }
    Box(
        contentAlignment = Alignment.Center,
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
            }
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