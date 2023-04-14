package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.positionChange
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

class SamplePointerChangeInPagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SamplePointerChangeInPager()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SamplePointerChangeInPager(
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        pageCount = 10,
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