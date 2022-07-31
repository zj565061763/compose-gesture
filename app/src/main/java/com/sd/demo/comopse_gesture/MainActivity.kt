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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme
import com.sd.lib.compose.gesture.fAwaitAllPointersUp
import com.sd.lib.compose.gesture.fAwaitDowns
import com.sd.lib.compose.gesture.fOnPointerChange
import com.sd.lib.compose.gesture.fOnPointerDownChange

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
                    SampleOnPointerChange()
                }
            }
        }
    }
}

@Composable
private fun SampleOnPointerDownChange(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fOnPointerDownChange {
                logMsg { "fOnPointerDownChange size:${it.size}" }
            }
    )
}

@Composable
private fun SampleOnPointerChange(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .fOnPointerChange(
                onStart = {
                    logMsg { "onPointerChange onStart" }
                },
                onDown = {
                    logMsg { "onPointerChange onDown id:${it.id} ${it}" }
                },
                onUp = {
                    logMsg { "onPointerChange onUp id:${it.id} ${it}" }
                },
                onFinish = {
                    logMsg { "onPointerChange onFinish" }
                },
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
                        logMsg { "fAwaitDown" }

                        fAwaitAllPointersUp()
                        logMsg { "fAwaitAllPointersUp" }
                    }
                }
            }
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