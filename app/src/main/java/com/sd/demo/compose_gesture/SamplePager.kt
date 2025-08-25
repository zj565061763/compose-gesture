package com.sd.demo.compose_gesture

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sd.demo.compose_gesture.ui.theme.AppTheme
import com.sd.lib.compose.gesture.fConsume
import com.sd.lib.compose.gesture.fHasConsumedPositionChange
import com.sd.lib.compose.gesture.fPointer
import kotlin.math.absoluteValue

class SamplePager : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        Sample()
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Sample(
  modifier: Modifier = Modifier,
) {
  val pagerState = rememberPagerState { 10 }

  HorizontalPager(
    modifier = modifier,
    state = pagerState,
  ) { index ->
    if (index == 0) {
      Column {
        HeaderView()
        VerticalListView(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          count = 100,
        )
      }
    } else {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = index.toString())
      }
    }
  }
}

@Composable
private fun HeaderView(
  modifier: Modifier = Modifier,
) {
  var isDrag by remember { mutableStateOf(false) }
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(400.dp)
      .fPointer(
        touchSlop = 0f,
        onStart = {
          logMsg { "onStart" }
          isDrag = false
          calculatePan = true
        },
        onCalculate = {
          if (currentEvent.fHasConsumedPositionChange()) {
            cancelPointer()
            return@fPointer
          }

          if (!isDrag) {
            if (this.pan.x.absoluteValue >= this.pan.y.absoluteValue) {
              return@fPointer
            }
            isDrag = true
            logMsg { "drag" }
          }

          if (isDrag) {
            currentEvent.fConsume { it.positionChanged() }
          }
        },
        onFinish = {
          if (isCanceled) {
            logMsg { "onFinish canceled" }
          } else {
            logMsg { "onFinish" }
          }
        },
      ),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = "Header")
  }
}

@Composable
private fun VerticalListView(
  modifier: Modifier = Modifier,
  count: Int,
) {
  val context = LocalContext.current
  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Gray),
  ) {
    items(count) { index ->
      Button(
        onClick = {
          Toast.makeText(context, index.toString(), Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(text = index.toString())
      }
    }
  }
}