package com.example.electricitymonitoringscreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ElectricityViewModel : ViewModel() {

    private val _currentReading = MutableStateFlow(0.0)
    val currentReading = _currentReading.asStateFlow()

    private val _highestReading = MutableStateFlow(0.0)
    val highestReading = _highestReading.asStateFlow()

    private val _readingsList = MutableStateFlow<List<Double>>(emptyList())
    val readingsList = _readingsList.asStateFlow()

    private val _status =
        MutableStateFlow("Ready to start monitoring")
    val status = _status.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring = _isMonitoring.asStateFlow()

    private var monitoringJob: Job? = null

    // Generate readings once, at one-second intervals.
    private val readings = flow {
        val values = listOf(
            3.2, 4.5, 5.8, 6.4,
            4.0, 7.2, 5.5, 8.1
        )

        for (value in values) {
            emit(value)
            delay(1000)
        }
    }

    fun startMonitoring() {
        if (_isMonitoring.value) return

        _currentReading.value = 0.0
        _highestReading.value = 0.0
        _readingsList.value = emptyList()
        _isMonitoring.value = true
        _status.value = "Loading readings..."

        monitoringJob = viewModelScope.launch {
            try {
                readings
                    .filter { it > 5.0 }
                    .map { it * 1000.0 }
                    .collect { watts ->

                        _currentReading.value = watts

                        if (watts > _highestReading.value) {
                            _highestReading.value = watts
                        }

                        _readingsList.value =
                            _readingsList.value + watts

                        _status.value = "Reading updated"
                    }

                _status.value = "All readings displayed"
            } finally {
                _isMonitoring.value = false
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        _isMonitoring.value = false
        _status.value = "Monitoring stopped"
    }

    fun refreshReading() {
        if (_isMonitoring.value) {
            _status.value = "Waiting for next reading..."
        } else {
            _status.value = "Press Start to begin monitoring"
        }
    }

    override fun onCleared() {
        monitoringJob?.cancel()
        super.onCleared()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ElectricityScreen()
            }
        }
    }
}

@Composable
fun ElectricityScreen(
    vm: ElectricityViewModel = viewModel()
) {
    val current by vm.currentReading.collectAsState()
    val highest by vm.highestReading.collectAsState()
    val readings by vm.readingsList.collectAsState()
    val status by vm.status.collectAsState()
    val isMonitoring by vm.isMonitoring.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚡ Electricity Monitor",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Reading (> 5000 W)")

                Text(
                    text = if (current == 0.0) {
                        "Waiting for reading..."
                    } else {
                        "%.0f W".format(current)
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Highest Reading")

                Text(
                    text = "%.0f W".format(highest),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Status", style = MaterialTheme.typography.titleMedium)
                Text(status)
                Text(
                    if (isMonitoring) {
                        "Monitoring is on"
                    } else {
                        "Monitoring is off"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.startMonitoring() },
            enabled = !isMonitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start")
        }

        OutlinedButton(
            onClick = { vm.refreshReading() },
            enabled = isMonitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh")
        }

        OutlinedButton(
            onClick = { vm.stopMonitoring() },
            enabled = isMonitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Readings > 5000 Watt",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(readings) { watts ->
                ListItem(
                    headlineContent = {
                        Text("%.0f W".format(watts))
                    },
                    supportingContent = {
                        Text("%.2f kW".format(watts / 1000.0))
                    }
                )
                HorizontalDivider()
            }
        }
    }
}