package com.sd.demo.comopse_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fOnPointerChange

class SampleOnPointerChangeInPagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SampleOnPointerChangeInPager()
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SampleOnPointerChangeInPager(
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        count = 10,
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .fOnPointerChange(
                    onStart = {
                        logMsg { "onPointerChange onStart" }
                    },
                    onDown = {
                        logMsg { "onPointerChange onDown isConsumed:${it.isConsumed}" }
                    },
                    onUp = {
                        logMsg { "onPointerChange onUp isConsumed:${it.isConsumed}" }
                    },
                    onMove = {
                        val dragAmount = it.positionChangeIgnoreConsumed()
                        logMsg { "onPointerChange onMove $dragAmount isConsumed:${it.isConsumed}" }
//                        it.consume()
                    },
                    onFinish = {
                        logMsg { "onPointerChange onFinish maxCount:$maxDownPointerCount" }
                    },
                )
        ) {
            Text(text = it.toString())
        }
    }
}