package util.functions.task.observer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.task.Task
import model.task.User
import util.functions.UserStatusDialog
import util.functions.space.SpaceSection
import util.parseColor
import view.observer.sortedByStatus

@Composable
fun UserTaskSection(
    user: User,
    tasksBySpace: Map<String, List<Task>>,
    totalTasks: Int,
    tasks: List<Task>,
    spaceTasksMap: Map<String, List<Task>>,
    spaceIdNameMap: Map<String, String>
) {
    var expanded by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.h5,
                    color = parseColor(user.color.toString()),
                    modifier = Modifier.clickable { expanded = !expanded }
                )

                Row {
                    Button(
                        onClick = { showStatusDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Estado")
                    }

                    Button(
                        onClick = { showContactDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Contacto")
                    }
                }
            }

            Text(
                text = "Tiene $totalTasks tareas asignada(s)",
                style = MaterialTheme.typography.body1,
                color = parseColor(user.color.toString())
            )

            AnimatedVisibility(visible = expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    tasksBySpace.forEach { (spaceName, tasks) ->
                        if (tasks.isNotEmpty()) {
                            val spaceId = spaceIdNameMap.entries.find { it.value == spaceName }?.key
                            val spaceTasks = if (spaceId != null) spaceTasksMap[spaceId] ?: emptyList() else emptyList()

                            SpaceSection(
                                spaceName = spaceName,
                                tasks = tasks.sortedByStatus(),
                                allSpaceTasks = spaceTasks
                            )
                        }
                    }
                }
            }
        }
    }

    if (showContactDialog) {
        ContactDialog(
            user = user,
            onDismiss = { showContactDialog = false }
        )
    }

    if (showStatusDialog) {
        UserStatusDialog(
            tasks = tasks,
            onDismiss = { showStatusDialog = false },
            userName = user.username
        )
    }
}