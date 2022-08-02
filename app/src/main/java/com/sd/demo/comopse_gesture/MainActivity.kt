package com.sd.demo.comopse_gesture

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fAwaitAllPointersUp
import com.sd.lib.compose.gesture.fAwaitDowns
import com.sd.lib.compose.gesture.fClick
import com.sd.lib.compose.gesture.fOnPointerChange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                // A surface container using the 'background' color from the theme
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

@Composable
private fun SampleOnClick(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fClick(
                onPress = {
                    logMsg { "onPress" }
                },
                onDoubleTap = {
                    logMsg { "onDoubleTap" }
                },
                onLongPress = {
                    logMsg { "onLongPress" }
                },
                onTap = {
                    logMsg { "onTap" }
                }
            )
    )
}

@Composable
private fun SampleAwait(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        fAwaitDowns(count = 2)
                        logMsg { "fAwaitDowns" }

                        fAwaitAllPointersUp()
                        logMsg { "fAwaitAllPointersUp" }
                    }
                }
            }
    )
}

@Composable
private fun SampleOnPointerChange(
    modifier: Modifier = Modifier,
    text: String = "",
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
                    logMsg { "onPointerChange onDown count:$downPointerCount" }
                },
                onUp = {
                    logMsg { "onPointerChange onUp count:$downPointerCount" }
                },
                onMove = {
                    logMsg { "onPointerChange onMove" }
                },
                onFinish = {
                    logMsg { "onPointerChange onFinish maxCount:$maxDownPointerCount" }
                },
            )
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SampleOnPointerChangeInPager(
    modifier: Modifier = Modifier,
) {
    HorizontalPager(count = 10, modifier = modifier.fillMaxSize()) {
        SampleOnPointerChange(text = it.toString())
    }
}

@Composable
private fun SampleVelocityTracker(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fOnPointerChange(
                onStart = {
                    enableVelocity = true
                },
                onUp = {
                    logMsg { "onUp ${it.id} ${getPointerVelocity(it.id)}" }
                },
            )
    )
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComopsegestureTheme {
        Greeting("Android")
    }
}

inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture", msg)
}