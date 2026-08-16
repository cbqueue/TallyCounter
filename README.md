# Tally Counter

A digital hand tally counter for Android. Press the volume buttons on your phone or the on-screen
buttons to increment and decrement a count that persists across app launches.

## Features

- **Volume-button control** — hardware volume-up/down keys drive the counter.
- **Persistent count** — survives app closures and device reboots (backed by Jetpack DataStore).
- **Haptic feedback** — a short vibration pulse on every tap for tactile confirmation.
- **Portrait-only, dark theme** — designed for one-handed use.
- **No network, no ads** — everything runs on-device.

## Building

Requires Android Studio or the Android SDK command-line tools with Java 17.

```bash
./gradlew assembleDebug
```

Install on a connected device:

```bash
./gradlew installDebug
```

## Tech Stack

| Layer        | Technology                                    |
|--------------|-----------------------------------------------|
| Language     | Kotlin                                        |
| UI           | Jetpack Compose (Material 3)                  |
| Architecture | MVVM (single-activity)                        |
| Persistence  | DataStore Preferences                         |
| Gradle       | Version Catalog (`gradle/libs.versions.toml`) |

## Architecture

```
MainActivity
  └─ CounterScreen        Compose UI (count display, +/- buttons, reset dialog)
       └─ CounterViewModel   StateFlow<Int>, increment / decrement / reset
            └─ CounterRepository  DataStore wrapper (load/save single int)
```

## License

This project is open source.

## History

I wanted to see if a local AI inference model could generate a simple functional Android app.

Short answer: Yes.

It came to pass that I needed a mechanism to help track a tally of persons in an auditorium. I could
have purchased a hand-counter or downloaded an available app, but I figured this would be a good use
case to test software development using a local AI model.

Here's my setup:

- OS: Manjaro Linux (don't laugh, it's been very stable as my daily driver)
- IDE: Android Studio 2026.1.3
- Agent: Claude Code 2.1.233
- Inference Engine: llama.cpp (built with SYCL)
- Model: Qwen3.6-27B-Q4_K_M.gguf
- GPU: Intel Arc Pro B70 (Why? 32GB VRAM for under $1000 USD)
- CPU: AMD Ryzen 9 5900XT with 64GB DDR4 RAM (dated, but still performant)
- Target Platform: Google Pixel 7 Pro

I won't go into a lot of detail, but here's the initial prompt:

```text
You are an expert Android developer. Write clean, modern Kotlin code using Jetpack Compose and
ViewModel for a single-screen Tally Counter app. The app must persist state across app closures
using DataStore or SharedPreferences (or SavedStateHandle).

Core App Requirements

UI Framework: Jetpack Compose (Single main screen).

Initial State: Load the last saved count on startup. If no saved state exists, start at 0.

Counting Rules:
Increment count on Volume Up.
Decrement count on Volume Down.
Never allow the count to drop below 0.

Virtual Controls:
On-screen Increment (+) button.
On-screen Decrement (-) button.
On-screen Reset (0) button to reset the count to 0.

Persistence:
Save the count immediately whenever it changes so it persists when the app closes and reopens.

Key Technical Implementation Details

Volume Button Interception:
Override onKeyDown in the Activity to capture KeyEvent.KEYCODE_VOLUME_UP and KeyEvent.KEYCODE_VOLUME_DOWN.
Consume the events so the phone's media volume does not change while using the counter.

State Management: Hold the integer count in a StateFlow inside a ViewModel.

Storage:
Use SharedPreferences or Jetpack DataStore inside a repository layer to save the integer value
synchronously or asynchronously on change.

Output Deliverables Needed from the AI:
build.gradle.kts dependencies snippet (Jetpack Compose, Lifecycle, DataStore).
MainActivity.kt containing the volume button override and Compose set-up.
CounterViewModel.kt containing the business logic, bounds checking (min value 0), and persistence calls.
CounterScreen.kt containing the Jetpack Compose UI (large number display, plus, minus, and clear buttons).
```

What popped out was actually buildable and functional, though it needed some aesthetic tweaking
(functional doesn't have to be beautiful).
