package util.functions.space

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.TaskController
import data.toTaskData
import model.space.Space
import model.task.Task
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.functions.task.TaskDialog
import util.functions.task.TaskEditDialog
import util.functions.task.TaskListForSpace

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SpaceItem(
    space: Space,
    listId: String,
    taskController: TaskController,
    onEdit: (Space) -> Unit,
    onDelete: (Space) -> Unit,
    onLoadTasks: (Space, (Result<List<Task>, NetworkError>) -> Unit) -> Unit
) {
    var tasksExpanded by remember { mutableStateOf(false) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showStateDialog by remember { mutableStateOf(false) }
    var showTaskDialog by remember { mutableStateOf(false) }

    var hovered by remember { mutableStateOf(false) }
    val hoverModifier = Modifier.onPointerEvent(
        PointerEventType.Move
    ) {
    }
        .onPointerEvent(PointerEventType.Enter) {
            hovered = true
        }
        .onPointerEvent(PointerEventType.Exit) {
            hovered = false
        }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .widthIn(min = 300.dp)
                .animateContentSize(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .then(hoverModifier),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = space.name,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                            .padding(end = 8.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hovered) {
                            Text(
                                text = "➕",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    showTaskDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✏️",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    onEdit(space)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🗑️",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    onDelete(space)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📊",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    showStateDialog = true
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = if (tasksExpanded)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Mostrar/ocultar tareas",
                            modifier = Modifier
                                .clickable {
                                    tasksExpanded = !tasksExpanded
                                    if (tasksExpanded && tasks.isEmpty()) {
                                        onLoadTasks(space) { result ->
                                            if (result is Result.Success) {
                                                tasks = result.data
                                            }
                                        }
                                    }
                                }
                                .padding(4.dp)
                        )
                    }
                }

                if (tasksExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskListForSpace(
                        tasks = tasks,
                        taskController = taskController,
                        onTaskUpdated = { updatedTask ->
                            tasks = tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                        },
                        onTaskDeleted = { deletedTask ->
                            tasks = tasks.filter { it.id != deletedTask.id }
                        },
                    )
                }

                if (showTaskDialog) {
                    TaskDialog(
                        listId = listId,
                        taskController = taskController,
                        task = null,
                        onDismiss = { showTaskDialog = false },
                        onConfirm = { newTask ->
                            tasks = tasks + newTask
                            showTaskDialog = false
                            taskController.getTasks(listId) { result ->
                                if (result is Result.Success) {
                                    tasks = result.data
                                }
                            }
                        }
                    )
                }
            }
        }

        taskToEdit?.let { task ->
            TaskEditDialog(
                task = task,
                onDismiss = { taskToEdit = null },
                onSave = { updatedTask ->
                    updatedTask.id?.let { s ->
                        taskController.updateTask(s, updatedTask.toTaskData()) { result ->
                            if (result is Result.Success) {
                                tasks = tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                            }
                        }
                    }
                    taskToEdit = null
                },
                taskController = taskController
            )
        }

        if (showStateDialog) {
            SpaceStatusDialog(
                spaceName = space.name,
                tasks = tasks,
                onDismiss = { showStateDialog = false }
            )
        }
    }
}