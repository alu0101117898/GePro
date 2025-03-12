package util.functions.task.observer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.space.Space
import model.task.Task

@Composable
fun ObserverViewContent(
    isLoading: Boolean,
    errorMessage: String,
    allTasks: List<Task>,
    spaces: List<Space>,
    listSpaceMap: Map<String, String>,
    spaceTasksMap: Map<String, List<Task>>
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Vista de Observador", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red)
            else -> {
                println("Total de tareas cargadas: ${allTasks.size}")
                val userTasks = allTasks
                    .filter { it.assignees?.isNotEmpty() == true }
                    .groupBy { it.assignees?.first() }

                println("Usuarios con tareas: ${userTasks.keys.size}")

                UserTasksOverview(userTasks, spaces, listSpaceMap, spaceTasksMap)
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}