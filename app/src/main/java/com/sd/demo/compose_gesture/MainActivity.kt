package com.sd.demo.compose_gesture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.demo.compose_gesture.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Content(
                    listActivity = listOf(
                        SampleAwait::class.java,
                        SampleClick::class.java,
                        SamplePointer::class.java,
                        SamplePager::class.java,
                        SampleGesture::class.java,
                    ),
                    onClickActivity = {
                        startActivity(Intent(this, it))
                    },
                )
            }
        }
    }
}

@Composable
private fun Content(
    listActivity: List<Class<out Activity>>,
    onClickActivity: (Class<out Activity>) -> Unit,
) {
    val onClickActivityUpdated by rememberUpdatedState(onClickActivity)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            listActivity,
            key = { it },
        ) { item ->
            Button(
                onClick = { onClickActivityUpdated(item) }
            ) {
                Text(text = item.simpleName)
            }
        }
    }
}

inline fun logMsg(block: () -> Any) {
    val msg = block().toString()
    Log.i("compose-gesture-demo", msg)
}