package com.example.tallycounter

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The Tally Counter Screen:
 *   ┌──────────────────────┐
 *   │ icon   title   reset │
 *   │                      │
 *   │      large count     │
 *   │                      │
 *   │    big + button      │
 *   │                      │
 *   │    small − button    │
 *   └──────────────────────┘
 */
@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val countState by viewModel.countState().collectAsState()
    val context = LocalContext.current
    val vibrator = remember { getVibrator(context) }

    var showResetDialog by remember { mutableStateOf(false) }

    fun pulse() {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {

            // top bar: icon | app name | reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // app icon (left)
                Image(
                    painter = painterResource(R.drawable.hand_counter),
                    contentDescription = "App icon",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // app name (center)
                Text(
                    text = "Tally Counter",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(32.dp))

                // reset button (right)
                TextButton(
                    onClick = {
                        pulse()
                        showResetDialog = true
                    },
                    modifier = Modifier
                ) {
                    Text(text = "Reset", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }

            // main column: count on top, buttons below
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // count display — takes all remaining space above buttons
                Text(
                    text = countState.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .wrapContentHeight(Alignment.CenterVertically)
                )

                // big + button — primary action
                CounterButton(
                    label = "+",
                    onClick = {
                        pulse()
                        viewModel.increment()
                    },
                    width = 240.dp,
                    height = 150.dp,
                    textSize = 72.sp,
                    variant = ButtonVariant.Primary
                )

                // spacing gap between + and − buttons
                Spacer(modifier = Modifier.height(48.dp))

                // small − button — secondary action
                CounterButton(
                    label = "−",
                    onClick = {
                        pulse()
                        viewModel.decrement()
                    },
                    width = 100.dp,
                    height = 64.dp,
                    textSize = 32.sp,
                    variant = ButtonVariant.Secondary
                )

                // bottom padding to push buttons up from the bottom edge
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        // reset confirmation dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset count to zero?") },
                text = { Text("This will set the count back to 0.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.reset()
                            showResetDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

enum class ButtonVariant { Primary, Secondary }

/**
 * Reusable counter button with configurable size and style.
 */
@Composable
private fun CounterButton(
    label: String,
    onClick: () -> Unit,
    width: Dp,
    height: Dp,
    textSize: TextUnit,
    variant: ButtonVariant,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.width(width).height(height),
        shape = RoundedCornerShape(24.dp),
        colors = when (variant) {
            ButtonVariant.Primary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Text(text = label, fontSize = textSize, fontWeight = FontWeight.Bold)
    }
}

/**
 * Returns a [Vibrator] instance.
 */
private fun getVibrator(context: Context): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
