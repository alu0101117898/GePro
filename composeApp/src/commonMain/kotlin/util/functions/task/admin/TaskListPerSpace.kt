package util.functions.task.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.TaskController
import model.task.Task


@Composable
fun TaskListForSpace(
    tasks: List<Task>,
    taskController: TaskController,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    teamMembers: List<model.User>
) {

    if (tasks.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(start = 32.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            tasks.forEach { task ->
                TaskListItem(
                    task = task,
                    taskController = taskController,
                    onTaskUpdated = onTaskUpdated,
                    onTaskDeleted = onTaskDeleted,
                    teamMembers = teamMembers
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        Text(
            text = "Este espacio no tiene ninguna tarea",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(16.dp)
        )
    }
}


