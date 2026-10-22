<img width="365" height="796" alt="image" src="https://github.com/user-attachments/assets/8613d14f-fe4e-489b-a0b1-3e33bb8a5b51" /># Smart-Electricity-Monitoring-Application

A simple Android electricity monitoring application built with **Kotlin** and **Jetpack Compose**. It demonstrates Kotlin **Flow**, **StateFlow**, `filter()`, and `map()` by processing simulated electricity readings and updating the UI automatically.

## Features

- **Live reading display:** Shows the latest reading that passes the filter.
- **Filter readings:** Displays only readings greater than **5 kW**.
- **Convert kW to W:** Uses `map { it * 1000.0 }` to convert kilowatts into watts.
- **Highest reading:** Tracks the highest filtered reading received so far.
- **Reading history:** Lists all filtered readings in watts and kilowatts.
- **Start, Refresh, and Stop controls:** Start monitoring, update the status message, or cancel monitoring.
- **Automatic UI updates:** `StateFlow` values are collected in Jetpack Compose using `collectAsState()`.

## Screenshots

-<img width="365" height="796" alt="image" src="https://github.com/user-attachments/assets/ee08f4be-e585-434c-94b6-aa9b52971102" />
-<img width="373" height="802" alt="image" src="https://github.com/user-attachments/assets/04bfdf40-80e8-497a-8633-f15e2349a917" />
-<img width="365" height="801" alt="image" src="https://github.com/user-attachments/assets/9ae3fdde-3544-4d51-8e0c-996c78f2a593" />


## Technology Stack

- Kotlin
- Android Studio
- Jetpack Compose
- Material 3
- Kotlin Coroutines and Flow
- AndroidX ViewModel

## How It Works

The app generates sample readings once per second:

`3.2, 4.5, 5.8, 6.4, 4.0, 7.2, 5.5, 8.1` kW

1. **`flow {}`** emits each sample reading at one-second intervals.
2. **`filter { it > 5.0 }`** keeps readings strictly greater than 5 kW.
3. **`map { it * 1000.0 }`** converts each remaining reading from kW to W.
4. **`collect {}`** receives each converted reading and updates the current reading, highest reading, history list, and status.
5. **`MutableStateFlow` / `StateFlow`** hold the UI state. The UI observes the exposed state flows and updates when values change.
6. **`viewModelScope`** runs the monitoring coroutine in the ViewModel's lifecycle. Pressing **Stop** cancels the monitoring job.

### Example Output

| Input (kW) | Result |
|---:|---:|
| 3.2 | Filtered out |
| 4.5 | Filtered out |
| 5.8 | 5800 W |
| 6.4 | 6400 W |
| 4.0 | Filtered out |
| 7.2 | 7200 W |
| 5.5 | 5500 W |
| 8.1 | 8100 W |

The highest filtered reading in this sample is **8100 W (8.1 kW)**.

## Run the Project

1. Clone or download this repository.
2. Open the project in **Android Studio**.
3. Allow Gradle to sync.
4. Run the app on an Android emulator or connected device.

> The Kotlin code uses Jetpack Compose and Material 3. Make sure your Android project has the relevant Compose, Lifecycle ViewModel Compose, and Kotlin Coroutines dependencies configured.

## Project Structure

The provided code contains:

- **`MainActivity`** — sets the Compose content and applies `MaterialTheme`.
- **`ElectricityViewModel`** — creates sample readings, filters and converts them, tracks the highest value, and manages monitoring.
- **`ElectricityScreen`** — displays the current/highest readings, status, controls, and reading history.

## Important Notes

- The readings are **simulated sample data**; the app does not connect to a real electricity meter or hardware sensor.
- The filter is strictly `> 5.0`, so a reading of exactly 5.0 kW would not be included.
- The sample flow emits eight readings and then completes. The app is a learning/demo project, not a continuous production monitoring system.
- The **Refresh** button updates the status text; it does not fetch a new reading immediately.

## Learning Outcomes

This project is useful for practicing:

- Creating and collecting Kotlin Flows.
- Filtering and transforming stream data.
- Managing observable UI state with StateFlow.
- Updating a Jetpack Compose UI reactively.
- Starting and cancelling coroutines with a ViewModel.

## Future Improvements

- Connect to a real meter, sensor, or API.
- Add a chart for reading history.
- Add configurable thresholds and alerts for high consumption.
- Store readings locally for historical reports.
- Add unit tests for filtering, conversion, and highest-reading logic.

---

**Project:** Electricity Monitoring Screen  
**Language:** Kotlin  
**UI:** Jetpack Compose + Material 3  
**Core concepts:** Flow, StateFlow, filter, map, coroutines
