package com.sd.demo.comopse_gesture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sd.demo.comopse_gesture.ui.theme.ComopsegestureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComopsegestureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        SampleButton(clazz = SampleAwaitActivity::class.java)
                        SampleButton(clazz = SampleClickActivity::class.java)
                        SampleButton(clazz = SamplePointerChangeActivity::class.java)
                        SampleButton(clazz = SamplePointerChangeInPagerActivity::class.java)
                        SampleButton(clazz = SampleOnScaleActivity::class.java)
                        SampleButton(clazz = SampleDragActivity::class.java)
                        SampleButton(clazz = SampleVelocityTrackerActivity::class.java)
                    }
                }
            }
        }
    }

    @Composable
    private fun SampleButton(
        modifier: Modifier = Modifier,
        clazz: Class<*>
    ) {
        Button(
            onClick = { startActivity(Intent(this@MainActivity, clazz)) },
            modifier = modifier,
        ) {
            Text(text = clazz.simpleName)
        }
    }
}


inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture", msg)
}