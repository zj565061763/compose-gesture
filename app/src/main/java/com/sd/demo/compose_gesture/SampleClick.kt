package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fPointer

class SampleClick : ComponentActivity() {
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
  var hasMove by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .fPointer(
        onStart = {
          logMsg { "onStart" }
          hasMove = false
        },
        onDown = { input ->
          if (input.isConsumed || pointerCount > 1) {
            cancelPointer()
          }
        },
        onMove = {
          hasMove = true
        },
        onUp = { input ->
          if (input.isConsumed) {
            cancelPointer()
          } else {
            if (!hasMove) {
              val clickTime = input.uptimeMillis - input.previousUptimeMillis
              if (clickTime < 200) {
                logMsg { "click" }
              }
            }
          }
        },
        onFinish = {
          if (isCanceled) {
            logMsg { "onFinish canceled" }
          } else {
            logMsg { "onFinish" }
          }
        }
      )
  )
}