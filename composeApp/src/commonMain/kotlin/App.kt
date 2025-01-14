package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import networking.ClickUpClient
import org.jetbrains.compose. ui.tooling.preview.Preview
import util.NetworkError
import util.Result
import util.TaskData

@Composable
@Preview
fun App(client: ClickUpClient) {
    val taskData = remember { mutableStateOf<Result<String, NetworkError>>(Result.Loading) }
    val jsonData = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        taskData.value = client.getClickUpTask("8696pjad1")
    }

    when (val result = taskData.value) {
        is Result.Loading -> CircularProgressIndicator()
        is Result.Success -> {
            val json = Json { ignoreUnknownKeys = true }
            val task = json.decodeFromString<TaskData>(result.data)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Name: ${task.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (task.description != null) {
                    Text(
                        text = "Description: ${task.description}",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
                if (task.creator != null) {
                    Text(
                        text = "Username: ${task.creator.username}",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
        is Result.Error -> Text(text = "Error: ${result.error}")
    }
    Text(text = jsonData.value)
}