package com.sd.demo.compose_gesture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fHasConsumedPositionChange
import com.sd.lib.compose.gesture.fPointer

class SamplePointer : ComponentActivity() {
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
  var offset by remember { mutableStateOf(Offset.Zero) }
  var scale by remember { mutableFloatStateOf(1f) }
  var rotation by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .fPointer(
        onStart = {
          calculatePan = true
          calculateZoom = true
          calculateRotation = true
        },
        onCalculate = {
          if (currentEvent.fHasConsumedPositionChange()) {
            cancelPointer()
            return@fPointer
          }

          if ((scale < 0.3f && zoom < 1f) || (scale > 5f && zoom > 1f)) {
            return@fPointer
          }

          offset += this.pan
          scale *= this.zoom
          rotation += this.rotation
        },
      ),
    contentAlignment = Alignment.Center,
  ) {
    Image(
      painter = painterResource(R.drawable.scale),
      contentDescription = "",
      contentScale = ContentScale.FillWidth,
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer {
          translationX = offset.x
          translationY = offset.y
          scaleX = scale
          scaleY = scale
          rotationZ = rotation
        }
    )
  }
}