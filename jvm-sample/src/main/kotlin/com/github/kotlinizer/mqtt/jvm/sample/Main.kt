package com.github.kotlinizer.mqtt.jvm.sample

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kotlinizer.mqtt.MqttConnectionConfig
import com.github.kotlinizer.mqtt.MqttConnectionStatus
import com.github.kotlinizer.mqtt.client.MqttClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val flowLogger = FlowLogger()
private val mqttClient = MqttClient(flowLogger)

fun main() = Window(title = "MQTT Client Sample") {
    val scope = rememberCoroutineScope()
    val connectionState = mqttClient.connectionStatusStateFlow.collectAsState()
    val logs = flowLogger.map { it.reversed() }.collectAsState(emptyList())
    MaterialTheme {
        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(connectionState.value.name)
                Button(
                    onClick = {
                        scope.launch {
                            mqttClient.connect(
                                MqttConnectionConfig(serverUri = "tcp://localhost:1883")
                            )
                        }
                    },
                    enabled = connectionState.value != MqttConnectionStatus.CONNECTED
                ) {
                    Text("CONNECT")
                }
                Button(
                    onClick = {
                        scope.launch {
                            mqttClient.disconnect()
                        }
                    },
                    enabled = connectionState.value == MqttConnectionStatus.CONNECTED
                ) {
                    Text("DISCONNECT")
                }
            }
            LazyColumn(
                reverseLayout = false,
                state = LazyListState()
            ) {
                items(logs.value) { log -> Text(log) }
            }
        }
    }
}
