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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fConsume
import com.sd.lib.compose.gesture.fOnScale

class SampleOnScaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
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
            .fOnScale { event, centroid, change ->
                logMsg { "scale centroid:$centroid change:$change" }
                scale *= change
                event.fConsume()
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