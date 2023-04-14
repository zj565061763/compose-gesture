package com.sd.demo.compose_gesture

import android.app.Activity
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sd.demo.compose_gesture.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Item(clazz = SampleAwaitActivity::class.java)
        Item(clazz = SampleClickActivity::class.java)
        Item(clazz = SamplePointerChangeActivity::class.java)
        Item(clazz = SampleGestureActivity::class.java)
        Item(clazz = SampleScaleActivity::class.java)
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    clazz: Class<*>,
) {
    val activity = LocalContext.current as Activity
    Button(
        onClick = { activity.startActivity(Intent(activity, clazz)) },
        modifier = modifier,
    ) {
        Text(text = clazz.simpleName)
    }
}

inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture-demo", msg)
}