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
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeInPagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SamplePointerChangeInPager()
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SamplePointerChangeInPager(
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
                .fPointerChange(
                    onStart = {
                        logMsg { "PointerChange onStart" }
                    },
                    onDown = {
                        logMsg { "PointerChange onDown isConsumed:${it.isConsumed}" }
                    },
                    onUp = {
                        logMsg { "PointerChange onUp isConsumed:${it.isConsumed}" }
                    },
                    onMove = {
                        val dragAmount = it.positionChangeIgnoreConsumed()
                        logMsg { "PointerChange onMove $dragAmount isConsumed:${it.isConsumed}" }
//                        it.consume()
                    },
                    onFinish = {
                        logMsg { "PointerChange onFinish maxCount:$maxDownPointerCount" }
                    },
                )
        ) {
            Text(text = it.toString())
        }
    }
}