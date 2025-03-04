package view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.TaskController
import data.toTaskData
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Status
import model.task.Task
import model.task.TaskDialog
import util.date.formatDate
import util.functions.spaces.TaskEditDialog
import util.errorhandling.Result

@Composable
fun TaskListForSpace(
    tasks: List<Task>,
    listId: String,
    taskController: TaskController,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
    onTaskCreated: (Task) -> Unit
) {

    var showTaskDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(start = 32.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        tasks.forEach { task ->
            TaskListItem(task = task,
                taskController = taskController,
                onTaskUpdated = onTaskUpdated,
                onTaskDeleted = onTaskDeleted
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { showTaskDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.h4)
        }
    }
    if (showTaskDialog) {
        TaskDialog(
            listId = listId,
            taskController = taskController,
            task = null,
            onDismiss = { showTaskDialog = false },
            onConfirm = { newTask ->
                onTaskCreated(newTask)
                showTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskListItem(
    task: Task,
    taskController: TaskController,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit
) {
    var showTaskMenu by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompletionMessage by remember { mutableStateOf(false) }

    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueLocalDate = task.dueDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val isDelayed = dueLocalDate != null && dueLocalDate < currentDate
    val isLastDay = dueLocalDate != null && dueLocalDate == currentDate

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showTaskMenu = true },
        elevation = 2.dp,
        backgroundColor = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = if (isDelayed) Color.Red.copy(alpha = 0.1f) else Color.DarkGray.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isDelayed) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = when {
                                isDelayed -> "Retrasada"
                                isLastDay -> "❗Último día"
                                else -> task.status?.status ?: "Sin estado"
                            },
                            color = when {
                                isDelayed -> Color.Red
                                isLastDay -> Color(0xFFFFA000) // Amarillo oscuro
                                else -> getStatusColor(task.status)
                            },
                            style = MaterialTheme.typography.caption.copy(fontSize = 12.sp)
                        )
                    }
                }

                Text(
                    text = task.dueDate?.let { formatDate(it, currentDate) } ?: "Sin fecha",
                    style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
                    color = if (isDelayed) Color.Red else Color.Gray
                )
            }

            task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                val showExpandButton = desc.length > 100 && !isExpanded

                Column {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.body2.copy(color = Color.DarkGray),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showExpandButton) {
                        TextButton(
                            onClick = { isExpanded = true },
                            modifier = Modifier.padding(0.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    DropdownMenu(
        expanded = showTaskMenu,
        onDismissRequest = { showTaskMenu = false }
    ) {
        DropdownMenuItem(onClick = {
            showTaskMenu = false
            showEditDialog = true
        }) {
            Text("✏️ Editar")
        }
        DropdownMenuItem(onClick = {
            showTaskMenu = false
            showDeleteDialog = true
        }) {
            Text("🗑️ Eliminar", color = Color.Red)
        }
    }
    if (showEditDialog) {
        TaskEditDialog(
            task = task,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTask ->
                task.id?.let { taskId ->
                    val taskData = updatedTask.toTaskData()
                    taskController.updateTask(taskId, taskData) { result ->
                        if (result is Result.Success) {
                            onTaskUpdated(result.data)
                            if (updatedTask.status?.status == "complete") {
                                showCompletionMessage = true
                            }
                        } else if (result is Result.Error) {
                            println("Error al actualizar la tarea: ${result.error}")
                        }
                    }
                }
                showEditDialog = false
            },
            taskController = taskController
        )
    }

    if (showCompletionMessage) {
        AlertDialog(
            onDismissRequest = { showCompletionMessage = false },
            title = { Text("Tarea completada") },
            text = { Text("¡La tarea '${task.name}' ha sido completada!") },
            confirmButton = {
                TextButton(onClick = { showCompletionMessage = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        task.id?.let { taskId ->
                            taskController.deleteTask(taskId) { result ->
                                if (result is Result.Success) {
                                    onTaskDeleted(task)
                                } else if (result is Result.Error) {
                                    println("Error al eliminar la tarea: ${result.error}")
                                }
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun getStatusColor(status: Status?): Color {
    return when (status?.status?.lowercase()) {
        "complete" -> Color(0xFF4CAF50)
        "in progress" -> Color(0xFF2196F3)
        "to do" -> Color(0xFFFF9800)
        else -> Color.DarkGray.copy(alpha = 0.8f)
    }
}