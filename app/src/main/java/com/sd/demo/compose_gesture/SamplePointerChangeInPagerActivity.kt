package com.sd.demo.compose_gesture

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
import androidx.compose.ui.input.pointer.positionChange
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeInPagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
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
                    onMove = {
                        val change = it.positionChange()
                        logMsg { "PointerChange onMove change:$change" }
//                        it.consume()
                    },
                )
        ) {
            Text(text = it.toString())
        }
    }
}