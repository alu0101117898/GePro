package util.functions.task.resource

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controller.TaskController
import model.task.Task
import util.errorhandling.Result

@Composable
fun ResourceTasksView(
    currentUserId: Int,
    taskController: TaskController,
    onTaskStateChanged: (Task) -> Unit
) {
    var assignedTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(currentUserId) {
        taskController.getTasks("dummyListId") { result ->
            when(result) {
                is Result.Success -> {
                    assignedTasks = result.data.filter { task ->
                        task.assignees?.any { it.id == currentUserId } == true
                    }
                    errorMessage = ""
                }
                is Result.Error -> errorMessage = "Error al cargar tareas: ${result.error}"
                else -> {}
            }
            isLoading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text(
            text = "Tareas asignadas",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        } else {
            if (assignedTasks.isEmpty()) {
                Text(text = "No tienes tareas asignadas.")
            } else {
                assignedTasks.forEach { task ->
                    ResourceTaskItem(
                        task = task,
                        taskController = taskController,
                        onTaskStateChanged = onTaskStateChanged
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
