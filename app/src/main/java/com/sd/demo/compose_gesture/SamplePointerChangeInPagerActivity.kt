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
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointerChange

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Sample(
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        pageCount = 10,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .fPointerChange(
                    onStart = {
                        calculatePan = true
                    },
                    onMove = {
                        logMsg { "onMove" }
//                        it.consume()
                    },
                )
        ) {
            Text(text = it.toString())
        }
    }
}