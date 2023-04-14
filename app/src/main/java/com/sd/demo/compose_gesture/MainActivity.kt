package com.sd.demo.compose_gesture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sd.demo.compose_gesture.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        SampleButton(clazz = SampleAwaitActivity::class.java)
                        SampleButton(clazz = SampleClickActivity::class.java)
                        SampleButton(clazz = SamplePointerChangeActivity::class.java)
                        SampleButton(clazz = SamplePointerChangeVelocityActivity::class.java)
                        SampleButton(clazz = SamplePointerChangeInPagerActivity::class.java)
                        SampleButton(clazz = SampleGestureActivity::class.java)
                        SampleButton(clazz = SampleScaleActivity::class.java)
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
    Log.i("compose-gesture-demo", msg)
}