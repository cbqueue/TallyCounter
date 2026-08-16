package com.example.tallycounter

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier

/**
 * Entry point for the Tally Counter app.
 *
 * Key responsibilities:
 * 1. Override onKeyDown to intercept volume-up / volume-down
 *    so they drive the counter (not the media volume).
 * 2. Wire up the Compose UI with the ViewModel.
 * 3. Lock to portrait orientation.
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Force portrait-only orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val repository = CounterRepository(applicationContext)
        viewModel = CounterViewModel(repository)

        // Disable the system volume overlay so that volume key presses
        // do not trigger the built-in volume UI.
        volumeControlStream = 0 // AudioManager.STREAM_NONE

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CounterScreen(viewModel)
                }
            }
        }
    }

    /**
     * Intercepts volume-up and volume-down key presses to control the counter.
     * All other keys are passed to the system.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                viewModel.increment()
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                viewModel.decrement()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
