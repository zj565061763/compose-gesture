package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fClick
import com.sd.lib.compose.gesture.fCombinedClick

class SampleClickActivity : ComponentActivity() {
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
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray)
                .fClick {
                    logMsg { "fClick" }
                }
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.DarkGray)
                .fCombinedClick(
                    onPress = {
                        logMsg { "onPress" }
                    },
                    onClick = {
                        logMsg { "onClick" }
                    },
                    onDoubleClick = {
                        logMsg { "onDoubleClick" }
                    },
                    onLongClick = {
                        logMsg { "onLongClick" }
                    },
                )
        )
    }
}